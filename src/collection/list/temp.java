import java.util.AbstractList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
/**
 * ������
 *  List�ӿڿɵ�����С������ʵ�֡�ʵ�����п�ѡ��List����������������Ԫ�أ�����null��Ԫ�ؿ��ظ���
 *  �����б�ӿ��⣬�����ṩ��һ�ַ���������������Ĵ�С���洢���б��е�����Ĵ�С��
 *
 * ʱ�临�Ӷȣ�
 *  ����size��isEmpty��get��set��iterator��listIterator�ĵ����ǳ���ʱ��ġ�
 *  ���ɾ����ʱ�临�Ӷ�ΪO(N)���������в���Ҳ��������ʱ�临�Ӷȡ�
 *
 * ������
 *  ÿ��ArrayList����������������С����ΪListԪ�صĳ��ȣ�Ĭ�ϳ�ʼ��Ϊ10��
 *  ���������Զ�������
 *  �����ǰ֪������Ԫ�ؽ϶࣬���������Ԫ��ǰͨ������ensureCapacity()������ǰ���������Լ�С���������Զ������Ŀ�����
 *  Ҳ����ͨ������ʼ�����Ĺ�������ʼ�����������
 *
 * �̲߳���ȫ��
 *  ArrayList�����̰߳�ȫ�ġ�
 *  �����ҪӦ�õ����߳��У���Ҫ���ⲿ��ͬ��
 *
 * modCount��
 *  ������AbstractList�У�protected transient int modCount = 0;
 *  �Ѵӽṹ���޸Ĵ��б�Ĵ������ӽṹ���޸���ָ�����б�Ĵ�С�����ߴ����б��Ӷ�ʹ���ڽ��еĵ�����������Ľ����
 *  ���ֶ���iterator��listiterator�������صĵ��������б������ʵ��ʹ�á�
 *  �����������˴��ֶ��е�ֵ��������������б�����������׳�concurrentmodificationexception����Ӧnext��remove��previous��set��add������
 *  �ڵ����ڼ����ٲ����޸�ʱ�����ṩ�˿���ʧ�� ��Ϊ�������Ƿ�ȷ������Ϊ��
 *  �����Ƿ�ʹ�ô��ֶ��ǿ�ѡ�ġ�
 *  �������ϣ���ṩ����ʧ�ܵ����������б��������������ֻ������ add(int,e)��remove(int)�������Լ�������д�ġ������б�ṹ���޸ĵ��κ����������������Ӵ��ֶΡ�
 *  ��add(int, e)��remove(int)�ĵ�����������ֶ���ӵ��������ó��� 1����������������б�����������׳���ٵ� concurrentmodificationexceptions��
 *  ���ĳ��ʵ�ֲ�ϣ���ṩ����ʧ�ܵ�����������Ժ��Դ��ֶΡ�
 *
 * transient��
 *  Ĭ�������,��������г�Ա�����������־û�.��ĳЩ�����,����������־û������һЩ��Ա����,�����ʹ��transient�ؼ������������,transientҲ��java�еı�����(JDK 1.8)
 */
public class ArrayList<e> extends AbstractList<e> implements List<e>, RandomAccess, Cloneable, java.io.Serializable
{
    private static final long serialVersionUID = 8683452581122892189L;
    //Ĭ�ϳ�ʼ����
    private static final int DEFAULT_CAPACITY = 10;
    //���ڿ�ʵ�����������ʵ����
    private static final Object[] EMPTY_ELEMENTDATA = {};
    //Ĭ�ϵĿ�����
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    //�Եģ����Ԫ�ص����飬������Ȩ��
    transient Object[] elementData;
    //��С����������ʱJava�Ὣint��ʼ��Ϊ0
    private int size;
    //��ָ���������ó�ʼ�������Ĺ��캯�����������׳��쳣
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+initialCapacity);
        }
    }
    //Ĭ�Ϲ��캯����ʹ�ÿ������ʼ��
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    //�Լ��ϵĵ���������˳�򣬹���һ�����м�����Ԫ�ص��б�
    public ArrayList(Collection<!--? extends E--> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toarray���ܣ�����أ������ض���[]����JAVA BUG���6260652��
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // ʹ�ÿ�����
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    //��Ϊ�������������ʵ��Ԫ�ص��������ڴ����ʱ�����Ե��ø÷���ɾ��Ԥ����λ�ã���������ΪԪ��ʵ��������
    //���ȷ����������Ԫ����ӽ���ʱҲ���Ե��ø÷�������Լ�ռ�
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
        }
    }
    //ʹ��ָ������������������
    public void ensureCapacity(int minCapacity) {
        //�������Ϊ�գ�����Ԥȡ0������ȥĬ��ֵ(10)
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)? 0: DEFAULT_CAPACITY;
        //����������Ԥ�����������ʹ�øò�����һ��������������
        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }
    //�������Ԫ��ʱ��ȷ����������
    private void ensureCapacityInternal(int minCapacity) {
        //ʹ��Ĭ��ֵ�Ͳ����нϴ�����Ϊ����Ԥ��ֵ
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    //�����������������������������������
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
    //�����������������ܻᵼ���ڴ����(VM�ڴ�����)
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    //������������ȷ�����������ٳ����ɲ���ָ����Ԫ�ص���Ŀ
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        //Ԥ����������һ��
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //ȡ������еĽϴ�ֵ
        if (newCapacity - minCapacity < 0)//��newCapacity<mincapacity newcapacity="minCapacity;" ��Ԥ��ֵ����Ĭ�ϵ����ֵ����Ƿ����="" if="" (newcapacity="" -="" max_array_size=""> 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    //����Ƿ��������û������������������ֵ(java�е�intΪ4�ֽڣ��������Ϊ0x7fffffff)��Ĭ�����ֵ
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) //���
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
    //���������С
    public int size() {
        return size;
    }
    //�Ƿ�Ϊ��
    public boolean isEmpty() {
        return size == 0;
    }
    //�Ƿ����һ���� ����bool
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    //����һ��ֵ�������״γ��ֵ�λ�ã�������Ƿ�Ϊnullʹ�ò�ͬ��ʽ�жϡ������ھͷ���-1��ʱ�临�Ӷ�ΪO(N)
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    //����һ��ֵ���������һ�γ��ֵ�λ�ã������ھͷ���-1��ʱ�临�Ӷ�ΪO(N)
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    //���ظ�����Ԫ�ر���û�б����ƣ����ƹ������鷢���ı���׳��쳣
    public Object clone() {
        try {
            ArrayList<!--?--> v = (ArrayList<!--?-->) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
    //ת��ΪObject���飬ʹ��Arrays.copyOf()����
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }
    //����һ�����飬ʹ������ʱȷ�����ͣ����������������б��е�����Ԫ�أ��ӵ�һ�����һ��Ԫ�أ�
    //���ص����������ɲ����ͱ������нϴ�ֵȷ��
    @SuppressWarnings("unchecked")
    public <t> T[] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }
    //����ָ��λ�õ�ֵ����Ϊ�����飬�����ٶ��ر��
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }
    //����ָ��λ�õ�ֵ�����ǻ������λ�����񳬳����鳤��
    public E get(int index) {
        rangeCheck(index);
        return elementData(index);
    }
    //����ָ��λ��Ϊһ����ֵ��������֮ǰ��ֵ���������λ���Ƿ񳬳����鳤��
    public E set(int index, E element) {
        rangeCheck(index);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
    //���һ��ֵ�����Ȼ�ȷ������
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);
        elementData[size++] = e;
        return true;
    }
    //ָ��λ�����һ��ֵ��������ӵ�λ�ú�����
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        ensureCapacityInternal(size + 1);
        //public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
        //src:Դ���飻 srcPos:Դ����Ҫ���Ƶ���ʼλ�ã� dest:Ŀ�����飻 destPos:Ŀ��������õ���ʼλ�ã� length:���Ƶĳ���
        System.arraycopy(elementData, index, elementData, index + 1,size - index);
        elementData[index] = element;
        size++;
    }
    //ɾ��ָ��λ�õ�ֵ��������ӵ�λ�ã�����֮ǰ��ֵ
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0) System.arraycopy(elementData, index+1, elementData, index,numMoved);
        elementData[--size] = null; //�������������ڻ���
        return oldValue;
    }
    //ɾ��ָ��Ԫ���״γ��ֵ�λ��
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    //����ɾ��ָ��λ�õ�ֵ��֮���Խп��٣�Ӧ���ǲ���Ҫ���ͷ���ֵ����Ϊֻ�ڲ�ʹ��
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
    //������飬��ÿһ��ֵ��Ϊnull,������������(��ͬ��reset������Ĭ�ϴ�С�иı�Ļ���������)
    public void clear() {
        modCount++;
        for (int i = 0; i < size; i++) elementData[i] = null;
        size = 0;
    }
    //���һ�����ϵ�Ԫ�ص�ĩ�ˣ���Ҫ��ӵļ���Ϊ�շ���false
    public boolean addAll(Collection<!--? extends E--> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }
    //����ͬ�ϣ���ָ��λ�ÿ�ʼ���
    public boolean addAll(int index, Collection<!--? extends E--> c) {
        rangeCheckForAdd(index);
        Object[] a = c.toArray();   //Ҫ��ӵ�����
        int numNew = a.length;      //Ҫ��ӵ����鳤��
        ensureCapacityInternal(size + numNew);  //ȷ������
        int numMoved = size - index;//�����ƶ��ĳ���(ǰ�β���)
        if (numMoved > 0)           //�в���Ҫ�ƶ��ģ���ͨ�������ƣ�������󲿷���Ҫ�ƶ����ƶ�����ȷλ��
            System.arraycopy(elementData, index, elementData, index + numNew,numMoved);
        System.arraycopy(a, 0, elementData, index, numNew); //�µ�������ӵ��ı���ԭ�����м�
        size += numNew;
        return numNew != 0;
    }
    //ɾ��ָ����ΧԪ�ء�����Ϊ��ʼɾ��λ�úͽ���λ��
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;  //��α����ĳ���
        System.arraycopy(elementData, toIndex, elementData, fromIndex,numMoved);
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }
    //������񳬳����鳤�� �������Ԫ��ʱ
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //����Ƿ����
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
    //�׳����쳣������
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }
    //ɾ��ָ�����ϵ�Ԫ��
    public boolean removeAll(Collection<!--?--> c) {
        Objects.requireNonNull(c);//�������Ƿ�Ϊnull
        return batchRemove(c, false);
    }
    //������ָ�����ϵ�Ԫ��
    public boolean retainAll(Collection<!--?--> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }
    /**
     * Դ���� BY http://anxpp.com/
     * @param complement trueʱ�����鱣��ָ��������Ԫ�ص�ֵ��Ϊfalseʱ������ɾ��ָ��������Ԫ�ص�ֵ��
     * @return �������ظ���Ԫ�ض��ᱻɾ��(�����ǽ�ɾ��һ�λ򼸴�)�����κ�ɾ���������᷵��true
     */
    private boolean batchRemove(Collection<!--?--> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            //�������飬�������������Ƿ������Ӧ��ֵ���ƶ�Ҫ������ֵ������ǰ�棬w���ֵΪҪ������Ԫ�ص�����
            //�򵥵㣺���������ͽ���ͬԪ���ƶ���ǰ�Σ���ɾ�����ͽ���ͬԪ���ƶ���ǰ��
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        }finally {//ȷ���쳣�׳�ǰ�Ĳ��ֿ�����������Ĳ�������δ�������Ĳ��ֻᱻ�ӵ�����
            //r!=size��ʾ���ܳ����ˣ�c.contains(elementData[r])�׳��쳣
            if (r != size) {
                System.arraycopy(elementData, r,elementData, w,size - r);
                w += size - r;
            }
            //���w==size����ʾȫ��Ԫ�ض������ˣ�����Ҳ��û��ɾ���������������Ի᷵��false����֮������true������������
            //��w!=size��ʱ�򣬼�ʹtry���׳��쳣��Ҳ����ȷ�����쳣�׳�ǰ�Ĳ�������Ϊwʼ��ΪҪ������ǰ�β��ֵĳ��ȣ�����Ҳ�����������
            if (w != size) {
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;//�ı�Ĵ���
                size = w;   //�µĴ�СΪ������Ԫ�صĸ���
                modified = true;
            }
        }
        return modified;
    }
    //��������ʵ����״̬��һ�������������л�����д��������鱻���Ļ��׳��쳣
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
        int expectedModCount = modCount;
        s.defaultWriteObject(); //ִ��Ĭ�ϵķ����л�/���л����̡�����ǰ��ķǾ�̬�ͷ�˲̬�ֶ�д�����
        // д���С
        s.writeInt(size);
        // ��˳��д������Ԫ��
        for (int i=0; i<size; i&#43;&#43;)="" {="" s.writeobject(elementdata[i]);="" }="" if="" (modcount="" !="expectedModCount)" throw="" new="" concurrentmodificationexception();="" ������д��������Ƕ��ˡ�="" private="" void="" readobject(java.io.objectinputstream="" s)="" throws="" java.io.ioexception,="" classnotfoundexception="" elementdata="EMPTY_ELEMENTDATA;" ִ��Ĭ�ϵ����л�="" �����л�����="" s.defaultreadobject();="" �������鳤��="" s.readint();="" (size=""> 0) {
            ensureCapacityInternal(size);
            Object[] a = elementData;
            //��������Ԫ��
            for (int i=0; i<size; i&#43;&#43;)="" {="" a[i]="s.readObject();" }="" ����listiterator����ʼλ��Ϊָ������="" public="" listiterator<e=""> listIterator(int index) {
                if (index < 0 || index > size)
                    throw new IndexOutOfBoundsException("Index: "+index);
                return new ListItr(index);
            }
            //����ListIterator����ʼλ��Ϊ0
            public ListIterator<e> listIterator() {
                return new ListItr(0);
            }
            //������ͨ������
            public Iterator<e> iterator() {
                return new Itr();
            }
            //ͨ�õĵ�����ʵ��
            private class Itr implements Iterator<e> {
                int cursor;       //�α꣬��һ��Ԫ�ص�������Ĭ�ϳ�ʼ��Ϊ0
                int lastRet = -1; //�ϴη��ʵ�Ԫ�ص�λ��
                int expectedModCount = modCount;//�������̲������޸����飬������׳��쳣
                //�Ƿ�����һ��
                public boolean hasNext() {
                    return cursor != size;
                }
                //��һ��Ԫ��
                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();//��������Ƿ��޸�
                    int i = cursor;
                    if (i >= size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1; //����ƶ��α�
                    return (E) elementData[lastRet = i];    //���÷��ʵ�λ�ò��������ֵ
                }
                //ɾ��Ԫ��
                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();//��������Ƿ��޸�
                    try {
                        ArrayList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                @Override
                @SuppressWarnings("unchecked")
                public void forEachRemaining(Consumer<!--? super E--> consumer) {
                    Objects.requireNonNull(consumer);
                    final int size = ArrayList.this.size;
                    int i = cursor;
                    if (i >= size) {
                        return;
                    }
                    final Object[] elementData = ArrayList.this.elementData;
                    if (i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[i++]);
                    }
                    cursor = i;
                    lastRet = i - 1;
                    checkForComodification();
                }
                //��������Ƿ��޸�
                final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            }
            //ListIterator������ʵ��
            private class ListItr extends Itr implements ListIterator<e> {
                ListItr(int index) {
                    super();
                    cursor = index;
                }
                public boolean hasPrevious() {
                    return cursor != 0;
                }
                public int nextIndex() {
                    return cursor;
                }
                public int previousIndex() {
                    return cursor - 1;
                }
                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayList.this.elementData;
                    if (i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[lastRet = i];
                }
                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();
                    try {
                        ArrayList.this.set(lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
                public void add(E e) {
                    checkForComodification();
                    try {
                        int i = cursor;
                        ArrayList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }
            }
            //����ָ����Χ��������
            public List<e> subList(int fromIndex, int toIndex) {
                subListRangeCheck(fromIndex, toIndex, size);
                return new SubList(this, 0, fromIndex, toIndex);
            }
            //��ȫ���
        static void subListRangeCheck(int fromIndex, int toIndex, int size) {
            if (fromIndex < 0)
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            if (toIndex > size)
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            if (fromIndex > toIndex)
                throw new IllegalArgumentException("fromIndex(" + fromIndex +
                        ") > toIndex(" + toIndex + ")");
        }
        //������
        private class SubList extends AbstractList<e> implements RandomAccess {
            private final AbstractList<e> parent;
            private final int parentOffset;
            private final int offset;
            int size;
            SubList(AbstractList<e> parent,int offset, int fromIndex, int toIndex) {
                this.parent = parent;
                this.parentOffset = fromIndex;
                this.offset = offset + fromIndex;
                this.size = toIndex - fromIndex;
                this.modCount = ArrayList.this.modCount;
            }
            public E set(int index, E e) {
                rangeCheck(index);
                checkForComodification();
                E oldValue = ArrayList.this.elementData(offset + index);
                ArrayList.this.elementData[offset + index] = e;
                return oldValue;
            }
            public E get(int index) {
                rangeCheck(index);
                checkForComodification();
                return ArrayList.this.elementData(offset + index);
            }
            public int size() {
                checkForComodification();
                return this.size;
            }
            public void add(int index, E e) {
                rangeCheckForAdd(index);
                checkForComodification();
                parent.add(parentOffset + index, e);
                this.modCount = parent.modCount;
                this.size++;
            }
            public E remove(int index) {
                rangeCheck(index);
                checkForComodification();
                E result = parent.remove(parentOffset + index);
                this.modCount = parent.modCount;
                this.size--;
                return result;
            }
            protected void removeRange(int fromIndex, int toIndex) {
                checkForComodification();
                parent.removeRange(parentOffset + fromIndex,parentOffset + toIndex);
                this.modCount = parent.modCount;
                this.size -= toIndex - fromIndex;
            }
            public boolean addAll(Collection<!--? extends E--> c) {
                return addAll(this.size, c);
            }
            public boolean addAll(int index, Collection<!--? extends E--> c) {
                rangeCheckForAdd(index);
                int cSize = c.size();
                if (cSize==0)
                    return false;
                checkForComodification();
                parent.addAll(parentOffset + index, c);
                this.modCount = parent.modCount;
                this.size += cSize;
                return true;
            }
            public Iterator<e> iterator() {
                return listIterator();
            }
            public ListIterator<e> listIterator(final int index) {
                checkForComodification();
                rangeCheckForAdd(index);
                final int offset = this.offset;
                return new ListIterator<e>() {
                    int cursor = index;
                    int lastRet = -1;
                    int expectedModCount = ArrayList.this.modCount;
                    public boolean hasNext() {
                        return cursor != SubList.this.size;
                    }
                    @SuppressWarnings("unchecked")
                    public E next() {
                        checkForComodification();
                        int i = cursor;
                        if (i >= SubList.this.size)
                            throw new NoSuchElementException();
                        Object[] elementData = ArrayList.this.elementData;
                        if (offset + i >= elementData.length)
                            throw new ConcurrentModificationException();
                        cursor = i + 1;
                        return (E) elementData[offset + (lastRet = i)];
                    }
                    public boolean hasPrevious() {
                        return cursor != 0;
                    }
                    @SuppressWarnings("unchecked")
                    public E previous() {
                        checkForComodification();
                        int i = cursor - 1;
                        if (i < 0)
                            throw new NoSuchElementException();
                        Object[] elementData = ArrayList.this.elementData;
                        if (offset + i >= elementData.length)
                            throw new ConcurrentModificationException();
                        cursor = i;
                        return (E) elementData[offset + (lastRet = i)];
                    }
                    @SuppressWarnings("unchecked")
                    public void forEachRemaining(Consumer<!--? super E--> consumer) {
                        Objects.requireNonNull(consumer);
                        final int size = SubList.this.size;
                        int i = cursor;
                        if (i >= size) {
                            return;
                        }
                        final Object[] elementData = ArrayList.this.elementData;
                        if (offset + i >= elementData.length) {
                            throw new ConcurrentModificationException();
                        }
                        while (i != size && modCount == expectedModCount) {
                            consumer.accept((E) elementData[offset + (i++)]);
                        }
                        // update once at end of iteration to reduce heap write traffic
                        lastRet = cursor = i;
                        checkForComodification();
                    }
                    public int nextIndex() {
                        return cursor;
                    }
                    public int previousIndex() {
                        return cursor - 1;
                    }
                    public void remove() {
                        if (lastRet < 0)
                            throw new IllegalStateException();
                        checkForComodification();
                        try {
                            SubList.this.remove(lastRet);
                            cursor = lastRet;
                            lastRet = -1;
                            expectedModCount = ArrayList.this.modCount;
                        } catch (IndexOutOfBoundsException ex) {
                            throw new ConcurrentModificationException();
                        }
                    }
                    public void set(E e) {
                        if (lastRet < 0)
                            throw new IllegalStateException();
                        checkForComodification();
                        try {
                            ArrayList.this.set(offset + lastRet, e);
                        } catch (IndexOutOfBoundsException ex) {
                            throw new ConcurrentModificationException();
                        }
                    }
                    public void add(E e) {
                        checkForComodification();
                        try {
                            int i = cursor;
                            SubList.this.add(i, e);
                            cursor = i + 1;
                            lastRet = -1;
                            expectedModCount = ArrayList.this.modCount;
                        } catch (IndexOutOfBoundsException ex) {
                            throw new ConcurrentModificationException();
                        }
                    }
                    final void checkForComodification() {
                        if (expectedModCount != ArrayList.this.modCount)
                            throw new ConcurrentModificationException();
                    }
                };
            }
            public List<e> subList(int fromIndex, int toIndex) {
                subListRangeCheck(fromIndex, toIndex, size);
                return new SubList(this, offset, fromIndex, toIndex);
            }
            private void rangeCheck(int index) {
                if (index < 0 || index >= this.size)
                    throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
            private void rangeCheckForAdd(int index) {
                if (index < 0 || index > this.size)
                    throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
            private String outOfBoundsMsg(int index) {
                return "Index: "+index+", Size: "+this.size;
            }
            private void checkForComodification() {
                if (ArrayList.this.modCount != this.modCount)
                    throw new ConcurrentModificationException();
            }
            public Spliterator<e> spliterator() {
                checkForComodification();
                return new ArrayListSpliterator<e>(ArrayList.this, offset,offset + this.size, this.modCount);
            }
        }
        @Override
        public void forEach(Consumer<!--? super E--> action) {
            Objects.requireNonNull(action);
            final int expectedModCount = modCount;
            @SuppressWarnings("unchecked")
            final E[] elementData = (E[]) this.elementData;
            final int size = this.size;
            for (int i=0; modCount == expectedModCount && i < size; i++) {
                action.accept(elementData[i]);
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        /**
         * Creates a <em>late-binding</em>
         * and <em>fail-fast</em> {@link Spliterator} over the elements in this
         * list.
         *
         * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
         * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
         * Overriding implementations should document the reporting of additional
         * characteristic values.
         *
         * @return a {@code Spliterator} over the elements in this list
         * @since 1.8
         */
        @Override
        public Spliterator<e> spliterator() {
            return new ArrayListSpliterator<>(this, 0, -1, 0);
        }
        /** Index-based split-by-two, lazily initialized Spliterator */
        static final class ArrayListSpliterator<e> implements Spliterator<e> {
            /*
             * If ArrayLists were immutable, or structurally immutable (no
             * adds, removes, etc), we could implement their spliterators
             * with Arrays.spliterator. Instead we detect as much
             * interference during traversal as practical without
             * sacrificing much performance. We rely primarily on
             * modCounts. These are not guaranteed to detect concurrency
             * violations, and are sometimes overly conservative about
             * within-thread interference, but detect enough problems to
             * be worthwhile in practice. To carry this out, we (1) lazily
             * initialize fence and expectedModCount until the latest
             * point that we need to commit to the state we are checking
             * against; thus improving precision.  (This doesn't apply to
             * SubLists, that create spliterators with current non-lazy
             * values).  (2) We perform only a single
             * ConcurrentModificationException check at the end of forEach
             * (the most performance-sensitive method). When using forEach
             * (as opposed to iterators), we can normally only detect
             * interference after actions, not before. Further
             * CME-triggering checks apply to all other possible
             * violations of assumptions for example null or too-small
             * elementData array given its size(), that could only have
             * occurred due to interference.  This allows the inner loop
             * of forEach to run without any further checks, and
             * simplifies lambda-resolution. While this does entail a
             * number of checks, note that in the common case of
             * list.stream().forEach(a), no checks or other computation
             * occur anywhere other than inside forEach itself.  The other
             * less-often-used methods cannot take advantage of most of
             * these streamlinings.
             */
            private final ArrayList<e> list;
            private int index; // current index, modified on advance/split
            private int fence; // -1 until used; then one past last index
            private int expectedModCount; // initialized when fence set
            /** Create new spliterator covering the given  range */
            ArrayListSpliterator(ArrayList<e> list, int origin, int fence,
                                 int expectedModCount) {
                this.list = list; // OK if null unless traversed
                this.index = origin;
                this.fence = fence;
                this.expectedModCount = expectedModCount;
            }
            private int getFence() { // initialize fence to size on first use
                int hi; // (a specialized variant appears in method forEach)
                ArrayList<e> lst;
                if ((hi = fence) < 0) {
                    if ((lst = list) == null)
                        hi = fence = 0;
                    else {
                        expectedModCount = lst.modCount;
                        hi = fence = lst.size;
                    }
                }
                return hi;
            }
            public ArrayListSpliterator<e> trySplit() {
                int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                return (lo >= mid) ? null : // divide range in half unless too small
                        new ArrayListSpliterator<e>(list, lo, index = mid,
                                expectedModCount);
            }
            public boolean tryAdvance(Consumer<!--? super E--> action) {
                if (action == null)
                    throw new NullPointerException();
                int hi = getFence(), i = index;
                if (i < hi) {
                    index = i + 1;
                    @SuppressWarnings("unchecked") E e = (E)list.elementData[i];
                    action.accept(e);
                    if (list.modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                    return true;
                }
                return false;
            }
            public void forEachRemaining(Consumer<!--? super E--> action) {
                int i, hi, mc; // hoist accesses and checks from loop
                ArrayList<e> lst; Object[] a;
                if (action == null)
                    throw new NullPointerException();
                if ((lst = list) != null && (a = lst.elementData) != null) {
                    if ((hi = fence) < 0) {
                        mc = lst.modCount;
                        hi = lst.size;
                    }
                    else
                        mc = expectedModCount;
                    if ((i = index) >= 0 && (index = hi) <= a.length) {
                        for (; i < hi; ++i) {
                            @SuppressWarnings("unchecked") E e = (E) a[i];
                            action.accept(e);
                        }
                        if (lst.modCount == mc)
                            return;
                    }
                }
                throw new ConcurrentModificationException();
            }
            public long estimateSize() {
                return (long) (getFence() - index);
            }
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
            }
        }
        @Override
        public boolean removeIf(Predicate<!--? super E--> filter) {
            Objects.requireNonNull(filter);
            // figure out which elements are to be removed
            // any exception thrown from the filter predicate at this stage
            // will leave the collection unmodified
            int removeCount = 0;
            final BitSet removeSet = new BitSet(size);
            final int expectedModCount = modCount;
            final int size = this.size;
            for (int i=0; modCount == expectedModCount && i < size; i++) {
                @SuppressWarnings("unchecked")
                final E element = (E) elementData[i];
                if (filter.test(element)) {
                    removeSet.set(i);
                    removeCount++;
                }
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            // shift surviving elements left over the spaces left by removed elements
            final boolean anyToRemove = removeCount > 0;
            if (anyToRemove) {
                final int newSize = size - removeCount;
                for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                    i = removeSet.nextClearBit(i);
                    elementData[j] = elementData[i];
                }
                for (int k=newSize; k < size; k++) {
                    elementData[k] = null;  // Let gc do its work
                }
                this.size = newSize;
                if (modCount != expectedModCount) {
                    throw new ConcurrentModificationException();
                }
                modCount++;
            }
            return anyToRemove;
        }
        @Override
        @SuppressWarnings("unchecked")
        public void replaceAll(UnaryOperator<e> operator) {
            Objects.requireNonNull(operator);
            final int expectedModCount = modCount;
            final int size = this.size;
            for (int i=0; modCount == expectedModCount && i < size; i++) {
                elementData[i] = operator.apply((E) elementData[i]);
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }
        @Override
        @SuppressWarnings("unchecked")
        public void sort(Comparator<!--? super E--> c) {
            final int expectedModCount = modCount;
            Arrays.sort((E[]) elementData, 0, size, c);
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }
    }