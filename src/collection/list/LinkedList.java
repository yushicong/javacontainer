package collection.list;

import java.util.*;
import java.util.function.Consumer;

/**
 *  (LinkedList) 双向链表实现了 List和Deque 接口,实现了所有的可选List操作，并许可所有元素，包括null
 *
 *  作为一个双向列表所有的操作都可以执行,操作索引列表来遍历LinkedList,从开始或者结束，无论哪个更接近指定的索引
 *
 *  注意:此实现不是同步的
 *
 *  如果多线程同时访问一个LinkedList,并且至少一个线程修改这个list的结构,一定要在外部做 synchronized同步 处理。
 *  (一个结构修改，可以是 adds 或者 deletes 一个或者多个元素;但仅仅修改一个元素的值不是一个结构的修改)
 *  通常同步是在一些自然封装的列表中完成的，如果没有这样的封装对象，那这个List列表应该被包装方法使用,最好创建时使用,放置意外的不同步访问List
 *  eg：List list = Collections.synchronizedList(new LinkedList(...));
 *
 *  迭代器 iterators 和 listIterators 是可以fail-fast(我这里理解是快速抛异常)：
 *  如果这个list在迭代器创建以后的任何时间内的结构发生里变化(以任何方式除了迭代器的delete或者add)，则会抛出一个异常ConcurrentModificationException。
 *  因此当并发修改时，迭代器快速失败和清理，胜过 选择任意的风险。不确定的行为未来待定。
 *
 *  注意：迭代器的快速失败行为是不能被保证的，一般来说，不可能做到担保不同步的并发修改抛出异常，迭代器抛出异常是力所能及处理，
 *  因此编写一个依赖于这个异常的程序是不正确的，这个快速失败行为应该仅被检测bug使用
 *
 *  Java Collections Framework(java集合框架)
 *
 * @author  Josh Bloch
 * @see     List
 * @see     ArrayList
 * @since 1.2
 * @param <E> 这个集合展示的元素类型
 */

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, java.io.Serializable
{
    //容量
    transient int size = 0;

    /**
     * 首节点
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * 尾节点
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /**
     * 构造一个空列表。
     */
    public LinkedList() {
    }

    /**
     * 构造一个包含指定 collection 中的元素的列表，这些元素按其 collection 的迭代器返回的顺序排列。
     *
     * @param  c the collection whose elements are to be placed into this list
     * @throws NullPointerException - 如果指定的 collection 为 null
     */
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * 链接 e 作为第一个节点
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<E>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }

    /**
     * 链接 e 作为最后一个节点
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<E>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 插入一个节点到费可用的节点succ之前
     */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<E>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 分离非空的第一个节点
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 分离非空的最后一个节点.
     */
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 分离非空节点
     */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 返回此列表的第一个元素
     * 接口 Deque<E> 中的 getFirst
     * @return 此列表的第一个元素
     * @throws NoSuchElementException - 如果此列表为空
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * 返回此列表的最后一个元素。
     * 接口 Deque<E> 中的 getLast
     * @return 此列表的最后一个元素
     * @throws NoSuchElementException - 如果此列表为空
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * 移除并返回此列表的第一个元素。
     * 接口 Deque<E> 中的 removeFirst
     * @return 此列表的第一个元素
     * @throws NoSuchElementException - 如果此列表为空
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * 移除并返回此列表的最后一个元素。
     * 接口 Deque<E> 中的 removeLast
     * @return 此列表的最后一个元素
     * @throws NoSuchElementException - 如果此列表为空
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    /**
     * 将指定元素插入此列表的开头。
     * 接口 Deque<E> 中的 addFirst
     * @param e - 要添加的元素
     */
    public void addFirst(E e) {
        linkFirst(e);
    }

    /**
     * 将指定元素添加到此列表的结尾。
     * 接口 Deque<E> 中的 addLast
     * @param e - 要添加的元素
     */
    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * 如果此列表包含指定元素，则返回 true。更确切地讲，当且仅当此列表包含至少一个满足 (o==null ? e==null : o.equals(e)) 的元素 e 时返回 true
     * 接口 Collection<E> 中的 contains
     * 接口 Deque<E> 中的 contains
     * 接口 List<E> 中的 contains
     * @param o - 要测试在此列表中是否存在的元素
     * @return 如果此列表包含指定元素，则返回 true
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * 返回此列表的元素数
     * 接口 Collection<E> 中的 size
     * 接口 Deque<E> 中的 size
     * 接口 List<E> 中的 size
     * 类 AbstractCollection<E> 中的 size
     *
     * @return 此列表的元素数
     */
    public int size() {
        return size;
    }

    /**
     * 将指定元素添加到此列表的结尾
     *
     * 此方法等效于 addLast(E)。
     * 接口 Collection<E> 中的 add
     * 接口 Deque<E> 中的 add
     * 接口 List<E> 中的 add
     * 接口 Queue<E> 中的 add
     * 类 AbstractList<E> 中的 add
     *
     * @param e - 要添加到此列表的元素
     * @return true（根据 Collection.add(E) 的规定）
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * 从此列表中移除首次出现的指定元素（如果存在）。如果列表不包含该元素，则不作更改。更确切地讲，移除具有满足
     * (o==null ? get(i)==null : * o.equals(get(i))) 的最低索引 i 的元素（如果存在这样的元素）。
     * 如果此列表已包含指定元素（或者此列表由于调用而发生更改），则返回 true。
     *
     * @param o - 要从此列表删除的元素，如果存在
     * @return 如果此列表包含指定元素，则返回 true
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 添加指定 collection 中的所有元素到此列表的结尾，顺序是指定 collection 的迭代器返回这些元素的顺序。
     * 如果指定的 collection 在操作过程中被修改，则此操作的行为是不确定的。
     * （注意，如果指定 collection 就是此列表并且非空，则此操作的行为是不确定的。）
     *
     * 接口 Collection<E> 中的 addAll
     * 接口 List<E> 中的 addAll
     * 类 AbstractCollection<E> 中的 addAll
     *
     * @param c - 包含要添加到此列表的元素的 collection
     * @return 如果此列表由于调用而更改，则返回 true
     * @throws NullPointerException - 如果指定的 collection 为 null
     */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * 将指定 collection 中的所有元素从指定位置开始插入此列表。
     * 移动当前在该位置上的元素（如果有），所有后续元素都向右移（增加其索引）。
     * 新元素将按由指定 collection 的迭代器返回的顺序在列表中显示。
     *
     * 接口 List<E> 中的 addAll
     * 类 AbstractSequentialList<E> 中的 addAll
     *
     * @param index - 在其中插入指定 collection 中第一个元素的索引
     * @param c - 包含要添加到此列表的元素的 collection
     * @return 如果此列表由于调用而更改，则返回 true
     * @throws IndexOutOfBoundsException - 如果索引超出范围 ( index < 0 || index > size())
     * @throws NullPointerException - 如果指定的 collection 为 null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;

        Node<E> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }

        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            Node<E> newNode = new Node<E>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

    /**
     * 从此列表中移除所有元素
     */
    public void clear() {
        // 清除所有的节点之间的联系是“不必要的”,但是:
        // - 帮助GC分代收集(减少对象大小)  如果丢弃的不在一个年代里，会受到很大影响
        // - 确保释放内存，即时有一个iterator
        for (Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }


    // 位置访问操作

    /**
     * 返回此列表中指定位置处的元素
     *
     * 接口 List<E> 中的 get
     * 类 AbstractSequentialList<E> 中的 get
     *
     * @param index - 要返回的元素的索引
     * @return 列表中指定位置的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    /**
     * 将此列表中指定位置的元素替换为指定的元素
     *
     * 接口 List<E> 中的 set
     * 类 AbstractSequentialList<E> 中的 set
     *
     * @param index - 要替换的元素的索引
     * @param element - 要在指定位置存储的元素
     * @return 以前在指定位置的元素
     * @throws IndexOutOfBoundsException - 如果索引超出范围 ( index < 0 || index >= size())
     */
    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    /**
     * 在此列表中指定的位置插入指定的元素。移动当前在该位置处的元素（如果有），所有后续元素都向右移（在其索引中添加 1）
     *
     * 接口 List<E> 中的 add
     * 类 AbstractSequentialList<E> 中的 add
     *
     * @param index - 要在其中插入指定元素的索引
     * @param element - 要插入的元素
     * @throws IndexOutOfBoundsException - 如果索引超出范围 ( index < 0 || index > size())
     */
    public void add(int index, E element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    /**
     * 移除此列表中指定位置处的元素。
     * 将任何后续元素向左移（从索引中减 1）。
     * 返回从列表中删除的元素。
     *
     * 接口 List<E> 中的 remove
     * 类 AbstractSequentialList<E> 中的 remove
     *
     * @param index - 要移除的元素的索引
     * @return 以前在指定位置的元素
     * @throws IndexOutOfBoundsException - 如果索引超出范围 ( index < 0 || index >= size())
     */
    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    /**
     * 检查索引是否超出范围，因为元素索引是0~size-1的，所以index必须满足0<=index<size
     */
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    /**
     * 检查位置是否超出范围，index必须在index~size之间（含），如果超出，返回false
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     * Of the many possible refactorings of the error handling code,
     * this "outlining" performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * 检查元素索引是否超出范围，若已超出，就抛出异常
     * @param index
     */
    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    /**
     * 检查位置是否超出范围，若已超出，就抛出异常
     * @param index
     */
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 获取指定位置的节点
     */
    Node<E> node(int index) {
        //如果位置索引小于列表长度的一半(或一半减一)，从前面开始遍历；否则，从后面开始遍历
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    // Search Operations

    /**
     * 返回此列表中首次出现的指定元素的索引，如果此列表中不包含该元素，则返回 -1。更确切地讲，返回满足 (o==null ? get(i)==null : o.equals(get(i))) 的最低索引 i；如果没有此索引，则返回 -1。
     * 接口 List<E> 中的 indexOf
     * 类 AbstractList<E> 中的 indexOf
     * @param o - 要搜索的元素
     * @return 此列表中首次出现的指定元素的索引，如果此列表中不包含该元素，则返回 -1
     *
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * 返回此列表中最后出现的指定元素的索引，如果此列表中不包含该元素，则返回 -1。
     * 更确切地讲，返回满足 (o==null ? get(i)==null : o.equals(get(i))) 的最高索引 i；
     * 如果没有此索引，则返回 -1
     *
     * 接口 List<E> 中的 lastIndexOf
     * 类 AbstractList<E> 中的 lastIndexOf
     *
     * @param o - 要搜索的元素
     * @return 此列表中最后出现的指定元素的索引；如果此列表中不包含该元素，则返回 -1
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

    // Queue operations.

    /**
     * 获取但不移除此列表的头（第一个元素）
     *
     * 接口 Deque<E> 中的 peek
     * 接口 Queue<E> 中的 peek
     *
     * @return 此列表的头，如果此列表为空，则返回 null
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 获取但不移除此列表的头（第一个元素）。
     *
     * 接口 Deque<E> 中的 element
     * 接口 Queue<E> 中的 element
     *
     * @return 列表的头
     * @throws NoSuchElementException - 如果此列表为空
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * 获取并移除此列表的头（第一个元素）
     *
     * 接口 Deque<E> 中的 poll
     * 接口 Queue<E> 中的 poll
     *
     * @return 此列表的头，如果此列表为空，则返回 null
     * @since 1.5
     */
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 获取并移除此列表的头（第一个元素）
     *
     * 接口 Deque<E> 中的 remove
     * 接口 Queue<E> 中的 remove
     *
     * @return 列表的头
     * @throws NoSuchElementException - 如果此列表为空

     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * 将指定元素添加到此列表的末尾（最后一个元素）
     *
     * 接口 Deque<E> 中的 offer
     * 接口 Queue<E> 中的 offer
     *
     * @param e - 要添加的元素
     * @return true（根据 Queue.offer(E) 的规定）
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }

    // Deque operations
    /**
     * 在此列表的开头插入指定的元素。
     *
     * @param e - 要插入的元素
     * @return true（根据 Deque.offerFirst(E) 的规定）
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * 在此列表末尾插入指定的元素
     *
     * 接口 Deque<E> 中的 offerLast
     *
     * @param e - 要插入的元素
     * @return true（根据 Deque.offerLast(E) 的规定）
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * 获取但不移除此列表的第一个元素；如果此列表为空，则返回 null。
     *
     * @return 此列表的第一个元素；如果此列表为空，则返回 null
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 获取但不移除此列表的最后一个元素；如果此列表为空，则返回 null。
     *
     * @return 此列表的最后一个元素；如果此列表为空，则返回 null
     * @since 1.6
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     * 获取并移除此列表的第一个元素；如果此列表为空，则返回 null。
     *
     * 接口 Deque<E> 中的 pollFirst
     *
     * @return 此列表的第一个元素；如果此列表为空，则返回 null
     * @since 1.6
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 获取并移除此列表的最后一个元素；如果此列表为空，则返回 null
     *
     * 接口 Deque<E> 中的 pollLast
     *
     * @return 此列表的最后一个元素；如果此列表为空，则返回 null
     * @since 1.6
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * 将元素推入此列表所表示的堆栈。换句话说，将该元素插入此列表的开头。
     *
     * 此方法等效于 addFirst(E)。
     *
     * 接口 Deque<E> 中的 push
     *
     * @param e - 要推入的元素
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * 从此列表所表示的堆栈处弹出一个元素。换句话说，移除并返回此列表的第一个元素。
     * 此方法等效于 removeFirst()。
     *
     * 接口 Deque<E> 中的 pop
     *
     * @return 此列表开头的元素（它是此列表所表示的堆栈的顶部）
     * @throws NoSuchElementException - 如果此列表为空
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }

    /**
     * 从此列表中移除第一次出现的指定元素（从头部到尾部遍历列表时）。如果列表不包含该元素，则不作更改。
     *
     * @param o element to be removed from this list, if present
     * @return {@code true} if the list contained the specified element
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * 从此列表中移除最后一次出现的指定元素（从头部到尾部遍历列表时）。如果列表不包含该元素，则不作更改。
     *
     * 接口 Deque<E> 中的 removeLastOccurrence
     *
     * @param o - 要从此列表中移除的元素（如果存在）
     * @return 如果该列表已包含指定元素，则返回 true
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回此列表中的元素的列表迭代器（按适当顺序），从列表中指定位置开始。遵守 List.listIterator(int) 的常规协定。
     * 列表迭代器是快速失败 的：在迭代器创建之后，如果从结构上对列表进行修改，除非通过列表迭代器自身的 remove 或 add 方法，
     * 其他任何时间任何方式的修改，列表迭代器都将抛出 ConcurrentModificationException。
     * 因此，面对并发的修改，迭代器很快就会完全失败，而不冒将来不确定的时间任意发生不确定行为的风险。
     *
     * 接口 List<E> 中的 listIterator
     * 类 AbstractSequentialList<E> 中的 listIterator
     *
     * @param index - 要从列表迭代器返回的第一个元素的索引（通过调用 next 方法）
     * @return 此列表中的元素的 ListIterator（按适当顺序），从列表中指定位置开始
     * @throws IndexOutOfBoundsException - 如果索引超出范围 ( index < 0 || index > size())
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * 内部初始化Node,参考都早方法
     * @param <E>
     */
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * 返回逆向迭代器
     * @since 1.6
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * 适配器提供下行迭代器
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<E> superClone() {
        try {
            return (LinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * 返回此 LinkedList 的浅表副本。（这些元素本身没有复制。）
     *
     * 类 Object 中的 clone
     *
     * @return 此 LinkedList 实例的浅表副本
     */
    public Object clone() {
        LinkedList<E> clone = superClone();

        // Put clone into "virgin" state
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        // Initialize clone with our elements
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    /**
     * 返回以适当顺序（从第一个元素到最后一个元素）包含此列表中所有元素的数组。
     * 由于此列表不维护对返回数组的任何引用，因而它将是“安全的”。（换句话说，此方法必须分配一个新数组）。因此，调用者可以随意修改返回的数组。
     * 此方法充当基于数组的 API 与基于 collection 的 API 之间的桥梁。
     *
     * 接口 Collection<E> 中的 toArray
     * 接口 List<E> 中的 toArray
     * 类 AbstractCollection<E> 中的 toArray
     *
     * @return 以适当顺序包含此列表中所有元素的数组。
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    /**
     * 返回以适当顺序（从第一个元素到最后一个元素）包含此列表中所有元素的数组；
     * 返回数组的运行时类型为指定数组的类型。如果指定数组能容纳列表，则在其中返回该列表。
     * 否则，分配具有指定数组的运行时类型和此列表大小的新数组。
     * 如果指定数组能容纳列表，并有剩余空间（即数组比列表元素多），则紧跟在列表末尾的数组元素会被设置为 null。
     * （只有 在调用者知道列表不包含任何 null 元素时，才可使用此方法来确定列表的长度。）
     * 像 toArray() 方法一样，此方法充当基于数组的 API 与基于 collection 的 API 之间的桥梁。
     * 更进一步说，此方法允许对输出数组的运行时类型上进行精确控制，在某些情况下，可以用来节省分配开销。
     * 假定 x 是只包含字符串的一个已知列表。以下代码可用来将该列表转储到一个新分配的 String 数组：
     * String[] y = x.toArray(new String[0]);
     * 注意， toArray(new Object[0]) 和 toArray() 在功能上是相同的。
     *
     * 接口 Collection<E> 中的 toArray
     * 接口 List<E> 中的 toArray
     * 类 AbstractCollection<E> 中的 toArray
     *
     * @param a - 要在其中存储列表元素的数组（如果它足够大）；否则，为其分配具有相同运行时类型的新数组
     * @return 包含列表元素的数组
     * @throws ArrayStoreException - 如果指定数组的运行时类型不是此列表中每个元素的运行时类型的超类型
     * @throws NullPointerException - 如果指定的数组为 null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * 保存这个状态下的实例变成二进制流存储(序列化)
     * @serialData The size of the list (the number of elements it
     *             contains) is emitted (int), followed by all of its
     *             elements (each an Object) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    /**
     * 重建这个实例从一个二进制流存储(反序列化)
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED} and
     * {@link Spliterator#ORDERED}.  Overriding implementations should document
     * the reporting of additional characteristic values.
     *
     * @implNote
     * The {@code Spliterator} additionally reports {@link Spliterator#SUBSIZED}
     * and implements {@code trySplit} to permit limited parallelism..
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new LLSpliterator<E>(this, -1, 0);
    }

    /** A customized variant of Spliterators.IteratorSpliterator */
    static final class LLSpliterator<E> implements Spliterator<E> {
        static final int BATCH_UNIT = 1 << 10;  // 批处理数组大小增加
        static final int MAX_BATCH = 1 << 25;  // 最大批量数组数
        final LinkedList<E> list; // null OK unless traversed
        Node<E> current;      // 当前节点; null 直到初始化
        int est;              // 大小估计; -1 直到第一次需要
        int expectedModCount; // 当est赋值时,初始化
        int batch;            // 批处理大小分割

        LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst() {
            int s; // force initialization
            final LinkedList<E> lst;
            if ((s = est) < 0) {
                if ((lst = list) == null)
                    s = est = 0;
                else {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }

        public long estimateSize() { return (long) getEst(); }

        /**
         * 这个方法用来分裂list的，s是最大可供split的元素数量
         * @return
         */
        public Spliterator<E> trySplit() {
            Node<E> p;
            int s = getEst();
            if (s > 1 && (p = current) != null) {
                // n是本list中需要被下一个splitter分裂的最大数目
                int n = batch + BATCH_UNIT;
                if (n > s)
                    n = s;
                if (n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do { a[j++] = p.item; } while ((p = p.next) != null && j < n);
                current = p;
                batch = j;
                // 总数是s，split掉了j个，当然还剩下s-j个可供split的元素
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }

        /**
         * 从current开始，一直到结束，每个元素都交给consumer来处理，显然处理完后est会变成0
         * @param action
         */
        public void forEachRemaining(Consumer<? super E> action) {
            Node<E> p; int n;
            if (action == null) throw new NullPointerException();
            if ((n = getEst()) > 0 && (p = current) != null) {
                current = null;
                est = 0;
                do {
                    E e = p.item;
                    p = p.next;
                    action.accept(e);
                } while (p != null && --n > 0);
            }
            if (list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        /**
         * 把当前的元素交给consumer处理，相当于向前advance了一位。处理成功返回true
         * @param action
         * @return
         */
        public boolean tryAdvance(Consumer<? super E> action) {
            Node<E> p;
            if (action == null) throw new NullPointerException();
            if (getEst() > 0 && (p = current) != null) {
                --est;
                E e = p.item;
                current = p.next;
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }
        /**
         * 说明这个splitter的特性的
         * ORDERED: 说明这个Spliterator关联的集合是有序的，也就是说
         *          trySplit(), tryAdvance以及forEachRemaining()方法
         *          处理的元素都是有序的(就像它们在集合里的顺序一样)
         * SIZED: 说明estimateSize()这个方法返回的值是有穷的
         * SUBSIZED: 说明基于本Spliterator的Spliterator也是SIZED
         */
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

}
