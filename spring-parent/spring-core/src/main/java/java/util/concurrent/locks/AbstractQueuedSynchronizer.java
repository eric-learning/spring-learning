/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.locks;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import sun.misc.Unsafe;
/**
 * 抽象的队列式同步器
 * AQS是一种提供了原子式管理同步状态、阻塞和唤醒线程功能以及队列模型的简单框架。
 * Java中的大部分同步类(Lock/Semaphore/ReentrantLock等)都是基于AbstractQueuedSynchronizer实现的
 *
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractQueuedSynchronizer
		extends AbstractOwnableSynchronizer
		implements java.io.Serializable {

	private static final long serialVersionUID = 7373984972572414691L;

	protected AbstractQueuedSynchronizer() { }

	static final class Node {
		/** 指示此节点正在共享模式下等待 */
		static final Node SHARED = new Node();
		/** 指示此节点正在独占模式下等待 */
		static final Node EXCLUSIVE = null;

		/** (waitStatus)表示当前节点已取消调度。当timeout或被中断（响应中断的情况下），会触发变更为此状态，进入该状态后的节点将不会再变化 */
		static final int CANCELLED =  1;
		/** (waitStatus)表示后继节点在等待当前节点唤醒。后继节点入队时，会将前继节点的状态更新为SIGNAL */
		static final int SIGNAL    = -1;
		/** (waitStatus)表示节点等待在Condition上，当其他线程调用了Condition的signal()方法后，CONDITION状态的节点将从等待队列转移到同步队列中，等待获取同步锁 */
		static final int CONDITION = -2;
		/**
		 * (waitStatus)共享模式下，前继节点不仅会唤醒其后继节点，同事也可能会唤醒后继的后继节点
		 */
		static final int PROPAGATE = -3;
		// 负值表示节点处于有效等待状态，而正值表示节点已被取消，所以源码中很多地方用>0、<0来判断节点的状态是否正常
		volatile int waitStatus;

		volatile Node prev;

		volatile Node next;

		volatile Thread thread;

		Node nextWaiter;

		/**
		 * 该节点正在共享模式下等待，则返回true.
		 */
		final boolean isShared() {
			return nextWaiter == SHARED;
		}

		/**
		 * predecessor:前任，前辈
		 * @return
		 * @throws NullPointerException
		 */
		final Node predecessor() throws NullPointerException {
			Node p = prev;
			if (p == null)
				throw new NullPointerException();
			else
				return p;
		}

		Node() {    // 用于建立初始头或共享标记
		}

		// 一个线程代表一个节点
		Node(Thread thread, Node mode) {     // 添加线程等待节点使用
			this.nextWaiter = mode;
			this.thread = thread;
		}

		Node(Thread thread, int waitStatus) { // Used by Condition
			this.waitStatus = waitStatus;
			this.thread = thread;
		}
	}

	private transient volatile Node head;

	private transient volatile Node tail;

	private volatile int state;

	protected final int getState() {
		return state;
	}

	protected final void setState(int newState) {
		state = newState;
	}

	protected final boolean compareAndSetState(int expect, int update) {
		return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
	}
	/** 旋转的超时阈值 */
	static final long spinForTimeoutThreshold = 1000L;

	/**
	 * 给双向链表在尾部添加Node
	 * @param node
	 * @return
	 */
	private Node enq(final Node node) {
		for (;;) {
			Node t = tail;
			if (t == null) { // Must initialize
				if (compareAndSetHead(new Node()))
					tail = head;
			} else {
				node.prev = t;
				if (compareAndSetTail(t, node)) {
					t.next = node;
					return t;
				}
			}
		}
	}

	/**
	 * 将当前线程加入到等待队列的队尾，并返回当前线程所在的结点
	 * @param mode
	 * @return
	 */
	private Node addWaiter(Node mode) {
		// 初始化节点，设置关联线程和模式（独占 or 共享）
		Node node = new Node(Thread.currentThread(), mode);
		// Try the fast path of enq; backup to full enq on failure
		Node pred = tail;
		// 尾结点不为空，说明队列已经初始化过
		if (pred != null) {
			node.prev = pred;
			if (compareAndSetTail(pred, node)) {
				pred.next = node;
				return node;
			}
		}
		// 尾结点为空，说明队列还未初始化，需要初始化head节点并入队新节点
		enq(node);
		return node;
	}

	private void setHead(Node node) {
		head = node;
		node.thread = null;
		node.prev = null;
	}

	private void unparkSuccessor(Node node) {
		int ws = node.waitStatus;
		if (ws < 0)
			compareAndSetWaitStatus(node, ws, 0);

		Node s = node.next;
		if (s == null || s.waitStatus > 0) {
			s = null;
			for (Node t = tail; t != null && t != node; t = t.prev)
				if (t.waitStatus <= 0)
					s = t;
		}
		if (s != null)
			LockSupport.unpark(s.thread);
	}

	private void doReleaseShared() {
		for (;;) {
			Node h = head;
			if (h != null && h != tail) {
				int ws = h.waitStatus;
				if (ws == Node.SIGNAL) {
					if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
						continue;            // loop to recheck cases
					unparkSuccessor(h);
				}
				else if (ws == 0 &&
						!compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
					continue;                // loop on failed CAS
			}
			if (h == head)                   // loop if head changed
				break;
		}
	}

	private void setHeadAndPropagate(Node node, int propagate) {
		Node h = head; // Record old head for check below
		setHead(node);
		if (propagate > 0 || h == null || h.waitStatus < 0 ||
				(h = head) == null || h.waitStatus < 0) {
			Node s = node.next;
			if (s == null || s.isShared())
				doReleaseShared();
		}
	}

	private void cancelAcquire(Node node) {
		// Ignore if node doesn't exist
		if (node == null)
			return;

		node.thread = null;

		// Skip cancelled predecessors
		Node pred = node.prev;
		while (pred.waitStatus > 0)
			node.prev = pred = pred.prev;

		Node predNext = pred.next;

		node.waitStatus = Node.CANCELLED;

		// If we are the tail, remove ourselves.
		if (node == tail && compareAndSetTail(node, pred)) {
			compareAndSetNext(pred, predNext, null);
		} else {
			// If successor needs signal, try to set pred's next-link
			// so it will get one. Otherwise wake it up to propagate.
			int ws;
			if (pred != head &&
					((ws = pred.waitStatus) == Node.SIGNAL ||
							(ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) &&
					pred.thread != null) {
				Node next = node.next;
				if (next != null && next.waitStatus <= 0)
					compareAndSetNext(pred, predNext, next);
			} else {
				unparkSuccessor(node);
			}

			node.next = node; // help GC
		}
	}

	private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
		int ws = pred.waitStatus;
		if (ws == Node.SIGNAL)
			return true;
		if (ws > 0) {
			do {
				node.prev = pred = pred.prev;
			} while (pred.waitStatus > 0);
			pred.next = node;
		} else {
			compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
		}
		return false;
	}

	/**
	 * Convenience method to interrupt current thread.
	 */
	static void selfInterrupt() {
		Thread.currentThread().interrupt();
	}

	/**
	 * Convenience method to park and then check if interrupted
	 *
	 * @return {@code true} if interrupted
	 */
	private final boolean parkAndCheckInterrupt() {
		LockSupport.park(this);
		return Thread.interrupted();
	}

	final boolean acquireQueued(final Node node, int arg) {
		boolean failed = true;
		try {
			boolean interrupted = false;
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null; // help GC
					failed = false;
					return interrupted;
				}
				if (shouldParkAfterFailedAcquire(p, node) &&
						parkAndCheckInterrupt())
					interrupted = true;
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	/**
	 * Acquires in exclusive interruptible mode.
	 * @param arg the acquire argument
	 */
	private void doAcquireInterruptibly(int arg)
			throws InterruptedException {
		final Node node = addWaiter(Node.EXCLUSIVE);
		boolean failed = true;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null; // help GC
					failed = false;
					return;
				}
				if (shouldParkAfterFailedAcquire(p, node) &&
						parkAndCheckInterrupt())
					throw new InterruptedException();
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	private boolean doAcquireNanos(int arg, long nanosTimeout)
			throws InterruptedException {
		if (nanosTimeout <= 0L)
			return false;
		final long deadline = System.nanoTime() + nanosTimeout;
		final Node node = addWaiter(Node.EXCLUSIVE);
		boolean failed = true;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head && tryAcquire(arg)) {
					setHead(node);
					p.next = null; // help GC
					failed = false;
					return true;
				}
				nanosTimeout = deadline - System.nanoTime();
				if (nanosTimeout <= 0L)
					return false;
				if (shouldParkAfterFailedAcquire(p, node) &&
						nanosTimeout > spinForTimeoutThreshold)
					LockSupport.parkNanos(this, nanosTimeout);
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	private void doAcquireShared(int arg) {
		final Node node = addWaiter(Node.SHARED);
		boolean failed = true;
		try {
			boolean interrupted = false;
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null; // help GC
						if (interrupted)
							selfInterrupt();
						failed = false;
						return;
					}
				}
				if (shouldParkAfterFailedAcquire(p, node) &&
						parkAndCheckInterrupt())
					interrupted = true;
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	private void doAcquireSharedInterruptibly(int arg)
			throws InterruptedException {
		final Node node = addWaiter(Node.SHARED);
		boolean failed = true;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null; // help GC
						failed = false;
						return;
					}
				}
				if (shouldParkAfterFailedAcquire(p, node) &&
						parkAndCheckInterrupt())
					throw new InterruptedException();
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	private boolean doAcquireSharedNanos(int arg, long nanosTimeout)
			throws InterruptedException {
		if (nanosTimeout <= 0L)
			return false;
		final long deadline = System.nanoTime() + nanosTimeout;
		final Node node = addWaiter(Node.SHARED);
		boolean failed = true;
		try {
			for (;;) {
				final Node p = node.predecessor();
				if (p == head) {
					int r = tryAcquireShared(arg);
					if (r >= 0) {
						setHeadAndPropagate(node, r);
						p.next = null; // help GC
						failed = false;
						return true;
					}
				}
				nanosTimeout = deadline - System.nanoTime();
				if (nanosTimeout <= 0L)
					return false;
				if (shouldParkAfterFailedAcquire(p, node) &&
						nanosTimeout > spinForTimeoutThreshold)
					LockSupport.parkNanos(this, nanosTimeout);
				if (Thread.interrupted())
					throw new InterruptedException();
			}
		} finally {
			if (failed)
				cancelAcquire(node);
		}
	}

	protected boolean tryAcquire(int arg) {
		throw new UnsupportedOperationException();
	}

	protected boolean tryRelease(int arg) {
		throw new UnsupportedOperationException();
	}

	protected int tryAcquireShared(int arg) {
		throw new UnsupportedOperationException();
	}

	protected boolean tryReleaseShared(int arg) {
		throw new UnsupportedOperationException();
	}

	protected boolean isHeldExclusively() {
		throw new UnsupportedOperationException();
	}

	public final void acquire(int arg) {
		if (!tryAcquire(arg) &&
				acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
			selfInterrupt();
	}

	public final void acquireInterruptibly(int arg)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (!tryAcquire(arg))
			doAcquireInterruptibly(arg);
	}

	public final boolean tryAcquireNanos(int arg, long nanosTimeout)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		return tryAcquire(arg) ||
				doAcquireNanos(arg, nanosTimeout);
	}

	public final boolean release(int arg) {
		if (tryRelease(arg)) {
			Node h = head;
			if (h != null && h.waitStatus != 0)
				unparkSuccessor(h);
			return true;
		}
		return false;
	}

	public final void acquireShared(int arg) {
		if (tryAcquireShared(arg) < 0)
			doAcquireShared(arg);
	}

	public final void acquireSharedInterruptibly(int arg)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		if (tryAcquireShared(arg) < 0)
			doAcquireSharedInterruptibly(arg);
	}

	public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
			throws InterruptedException {
		if (Thread.interrupted())
			throw new InterruptedException();
		return tryAcquireShared(arg) >= 0 ||
				doAcquireSharedNanos(arg, nanosTimeout);
	}

	public final boolean releaseShared(int arg) {
		if (tryReleaseShared(arg)) {
			doReleaseShared();
			return true;
		}
		return false;
	}

	public final boolean hasQueuedThreads() {
		return head != tail;
	}

	public final boolean hasContended() {
		return head != null;
	}

	public final Thread getFirstQueuedThread() {
		// handle only fast path, else relay
		return (head == tail) ? null : fullGetFirstQueuedThread();
	}

	/**
	 * Version of getFirstQueuedThread called when fastpath fails
	 */
	private Thread fullGetFirstQueuedThread() {
		Node h, s;
		Thread st;
		if (((h = head) != null && (s = h.next) != null &&
				s.prev == head && (st = s.thread) != null) ||
				((h = head) != null && (s = h.next) != null &&
						s.prev == head && (st = s.thread) != null))
			return st;


		Node t = tail;
		Thread firstThread = null;
		while (t != null && t != head) {
			Thread tt = t.thread;
			if (tt != null)
				firstThread = tt;
			t = t.prev;
		}
		return firstThread;
	}

	public final boolean isQueued(Thread thread) {
		if (thread == null)
			throw new NullPointerException();
		for (Node p = tail; p != null; p = p.prev)
			if (p.thread == thread)
				return true;
		return false;
	}

	final boolean apparentlyFirstQueuedIsExclusive() {
		Node h, s;
		return (h = head) != null &&
				(s = h.next)  != null &&
				!s.isShared()         &&
				s.thread != null;
	}

	public final boolean hasQueuedPredecessors() {
		// The correctness of this depends on head being initialized
		// before tail and on head.next being accurate if the current
		// thread is first in queue.
		Node t = tail; // Read fields in reverse initialization order
		Node h = head;
		Node s;
		return h != t &&
				((s = h.next) == null || s.thread != Thread.currentThread());
	}


	// Instrumentation and monitoring methods

	/**
	 * Returns an estimate of the number of threads waiting to
	 * acquire.  The value is only an estimate because the number of
	 * threads may change dynamically while this method traverses
	 * internal data structures.  This method is designed for use in
	 * monitoring system state, not for synchronization
	 * control.
	 *
	 * @return the estimated number of threads waiting to acquire
	 */
	public final int getQueueLength() {
		int n = 0;
		for (Node p = tail; p != null; p = p.prev) {
			if (p.thread != null)
				++n;
		}
		return n;
	}

	public final Collection<Thread> getQueuedThreads() {
		ArrayList<Thread> list = new ArrayList<Thread>();
		for (Node p = tail; p != null; p = p.prev) {
			Thread t = p.thread;
			if (t != null)
				list.add(t);
		}
		return list;
	}

	public final Collection<Thread> getExclusiveQueuedThreads() {
		ArrayList<Thread> list = new ArrayList<Thread>();
		for (Node p = tail; p != null; p = p.prev) {
			if (!p.isShared()) {
				Thread t = p.thread;
				if (t != null)
					list.add(t);
			}
		}
		return list;
	}

	public final Collection<Thread> getSharedQueuedThreads() {
		ArrayList<Thread> list = new ArrayList<Thread>();
		for (Node p = tail; p != null; p = p.prev) {
			if (p.isShared()) {
				Thread t = p.thread;
				if (t != null)
					list.add(t);
			}
		}
		return list;
	}

	public String toString() {
		int s = getState();
		String q  = hasQueuedThreads() ? "non" : "";
		return super.toString() +
				"[State = " + s + ", " + q + "empty queue]";
	}


	final boolean isOnSyncQueue(Node node) {
		if (node.waitStatus == Node.CONDITION || node.prev == null)
			return false;
		if (node.next != null) // If has successor, it must be on queue
			return true;
		return findNodeFromTail(node);
	}

	private boolean findNodeFromTail(Node node) {
		Node t = tail;
		for (;;) {
			if (t == node)
				return true;
			if (t == null)
				return false;
			t = t.prev;
		}
	}

	final boolean transferForSignal(Node node) {
		/*
		 * If cannot change waitStatus, the node has been cancelled.
		 */
		if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
			return false;

		Node p = enq(node);
		int ws = p.waitStatus;
		if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
			LockSupport.unpark(node.thread);
		return true;
	}

	final boolean transferAfterCancelledWait(Node node) {
		if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
			enq(node);
			return true;
		}
		while (!isOnSyncQueue(node))
			Thread.yield();
		return false;
	}

	final int fullyRelease(Node node) {
		boolean failed = true;
		try {
			int savedState = getState();
			if (release(savedState)) {
				failed = false;
				return savedState;
			} else {
				throw new IllegalMonitorStateException();
			}
		} finally {
			if (failed)
				node.waitStatus = Node.CANCELLED;
		}
	}

	public final boolean owns(ConditionObject condition) {
		return condition.isOwnedBy(this);
	}

	public final boolean hasWaiters(ConditionObject condition) {
		if (!owns(condition))
			throw new IllegalArgumentException("Not owner");
		return condition.hasWaiters();
	}

	public final int getWaitQueueLength(ConditionObject condition) {
		if (!owns(condition))
			throw new IllegalArgumentException("Not owner");
		return condition.getWaitQueueLength();
	}

	public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
		if (!owns(condition))
			throw new IllegalArgumentException("Not owner");
		return condition.getWaitingThreads();
	}

	public class ConditionObject implements Condition, java.io.Serializable {
		private static final long serialVersionUID = 1173984872572414699L;
		/** First node of condition queue. */
		private transient Node firstWaiter;
		/** Last node of condition queue. */
		private transient Node lastWaiter;

		/**
		 * Creates a new {@code ConditionObject} instance.
		 */
		public ConditionObject() { }

		// Internal methods

		/**
		 * Adds a new waiter to wait queue.
		 * @return its new wait node
		 */
		private Node addConditionWaiter() {
			Node t = lastWaiter;
			// If lastWaiter is cancelled, clean out.
			if (t != null && t.waitStatus != Node.CONDITION) {
				unlinkCancelledWaiters();
				t = lastWaiter;
			}
			Node node = new Node(Thread.currentThread(), Node.CONDITION);
			if (t == null)
				firstWaiter = node;
			else
				t.nextWaiter = node;
			lastWaiter = node;
			return node;
		}

		private void doSignal(Node first) {
			do {
				if ( (firstWaiter = first.nextWaiter) == null)
					lastWaiter = null;
				first.nextWaiter = null;
			} while (!transferForSignal(first) &&
					(first = firstWaiter) != null);
		}

		/**
		 * Removes and transfers all nodes.
		 * @param first (non-null) the first node on condition queue
		 */
		private void doSignalAll(Node first) {
			lastWaiter = firstWaiter = null;
			do {
				Node next = first.nextWaiter;
				first.nextWaiter = null;
				transferForSignal(first);
				first = next;
			} while (first != null);
		}

		private void unlinkCancelledWaiters() {
			Node t = firstWaiter;
			Node trail = null;
			while (t != null) {
				Node next = t.nextWaiter;
				if (t.waitStatus != Node.CONDITION) {
					t.nextWaiter = null;
					if (trail == null)
						firstWaiter = next;
					else
						trail.nextWaiter = next;
					if (next == null)
						lastWaiter = trail;
				}
				else
					trail = t;
				t = next;
			}
		}

		public final void signal() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node first = firstWaiter;
			if (first != null)
				doSignal(first);
		}

		public final void signalAll() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			Node first = firstWaiter;
			if (first != null)
				doSignalAll(first);
		}

		public final void awaitUninterruptibly() {
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			boolean interrupted = false;
			while (!isOnSyncQueue(node)) {
				LockSupport.park(this);
				if (Thread.interrupted())
					interrupted = true;
			}
			if (acquireQueued(node, savedState) || interrupted)
				selfInterrupt();
		}


		/** Mode meaning to reinterrupt on exit from wait */
		private static final int REINTERRUPT =  1;
		/** Mode meaning to throw InterruptedException on exit from wait */
		private static final int THROW_IE    = -1;

		private int checkInterruptWhileWaiting(Node node) {
			return Thread.interrupted() ?
					(transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) :
					0;
		}

		/**
		 * Throws InterruptedException, reinterrupts current thread, or
		 * does nothing, depending on mode.
		 */
		private void reportInterruptAfterWait(int interruptMode)
				throws InterruptedException {
			if (interruptMode == THROW_IE)
				throw new InterruptedException();
			else if (interruptMode == REINTERRUPT)
				selfInterrupt();
		}

		public final void await() throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				LockSupport.park(this);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null) // clean up if cancelled
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
		}

		public final long awaitNanos(long nanosTimeout)
				throws InterruptedException {
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			final long deadline = System.nanoTime() + nanosTimeout;
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (nanosTimeout <= 0L) {
					transferAfterCancelledWait(node);
					break;
				}
				if (nanosTimeout >= spinForTimeoutThreshold)
					LockSupport.parkNanos(this, nanosTimeout);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
				nanosTimeout = deadline - System.nanoTime();
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return deadline - System.nanoTime();
		}

		public final boolean awaitUntil(Date deadline)
				throws InterruptedException {
			long abstime = deadline.getTime();
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			boolean timedout = false;
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (System.currentTimeMillis() > abstime) {
					timedout = transferAfterCancelledWait(node);
					break;
				}
				LockSupport.parkUntil(this, abstime);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return !timedout;
		}

		public final boolean await(long time, TimeUnit unit)
				throws InterruptedException {
			long nanosTimeout = unit.toNanos(time);
			if (Thread.interrupted())
				throw new InterruptedException();
			Node node = addConditionWaiter();
			int savedState = fullyRelease(node);
			final long deadline = System.nanoTime() + nanosTimeout;
			boolean timedout = false;
			int interruptMode = 0;
			while (!isOnSyncQueue(node)) {
				if (nanosTimeout <= 0L) {
					timedout = transferAfterCancelledWait(node);
					break;
				}
				if (nanosTimeout >= spinForTimeoutThreshold)
					LockSupport.parkNanos(this, nanosTimeout);
				if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
					break;
				nanosTimeout = deadline - System.nanoTime();
			}
			if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
				interruptMode = REINTERRUPT;
			if (node.nextWaiter != null)
				unlinkCancelledWaiters();
			if (interruptMode != 0)
				reportInterruptAfterWait(interruptMode);
			return !timedout;
		}

		//  support for instrumentation

		/**
		 * Returns true if this condition was created by the given
		 * synchronization object.
		 *
		 * @return {@code true} if owned
		 */
		final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
			return sync == AbstractQueuedSynchronizer.this;
		}

		/**
		 * Queries whether any threads are waiting on this condition.
		 * Implements {@link AbstractQueuedSynchronizer#hasWaiters(ConditionObject)}.
		 *
		 * @return {@code true} if there are any waiting threads
		 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
		 *         returns {@code false}
		 */
		protected final boolean hasWaiters() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
				if (w.waitStatus == Node.CONDITION)
					return true;
			}
			return false;
		}

		/**
		 * Returns an estimate of the number of threads waiting on
		 * this condition.
		 * Implements {@link AbstractQueuedSynchronizer#getWaitQueueLength(ConditionObject)}.
		 *
		 * @return the estimated number of waiting threads
		 * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
		 *         returns {@code false}
		 */
		protected final int getWaitQueueLength() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			int n = 0;
			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
				if (w.waitStatus == Node.CONDITION)
					++n;
			}
			return n;
		}

		protected final Collection<Thread> getWaitingThreads() {
			if (!isHeldExclusively())
				throw new IllegalMonitorStateException();
			ArrayList<Thread> list = new ArrayList<Thread>();
			for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
				if (w.waitStatus == Node.CONDITION) {
					Thread t = w.thread;
					if (t != null)
						list.add(t);
				}
			}
			return list;
		}
	}
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long stateOffset;
	private static final long headOffset;
	private static final long tailOffset;
	private static final long waitStatusOffset;
	private static final long nextOffset;

	static {
		try {
			stateOffset = unsafe.objectFieldOffset
					(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
			headOffset = unsafe.objectFieldOffset
					(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
			tailOffset = unsafe.objectFieldOffset
					(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
			waitStatusOffset = unsafe.objectFieldOffset
					(Node.class.getDeclaredField("waitStatus"));
			nextOffset = unsafe.objectFieldOffset
					(Node.class.getDeclaredField("next"));

		} catch (Exception ex) { throw new Error(ex); }
	}

	/**
	 * CAS head field. Used only by enq.
	 * 头结点赋值
	 */
	private final boolean compareAndSetHead(Node update) {
		//当前的head字段，和null值比对，默认是null，所以相等，所以赋值为update，也就是new node()
		return unsafe.compareAndSwapObject(this, headOffset, null, update);
	}

	/**
	 * CAS tail field. Used only by enq.
	 * 尾结点赋值
	 */
	private final boolean compareAndSetTail(Node expect, Node update) {
		return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
	}

	/**
	 * CAS waitStatus field of a node.
	 */
	private static final boolean compareAndSetWaitStatus(Node node,
	                                                     int expect,
	                                                     int update) {
		return unsafe.compareAndSwapInt(node, waitStatusOffset,
				expect, update);
	}

	/**
	 * CAS next field of a node.
	 */
	private static final boolean compareAndSetNext(Node node,
	                                               Node expect,
	                                               Node update) {
		return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
	}
}