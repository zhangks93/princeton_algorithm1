import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] rqArrays;
    private int size;

    private class RandomIterator implements Iterator<Item> {
        private int rank; // rank 记录便利的次数
        private Item[] iterArrays; //两个迭代器必须相互独立，并且拥有自己的随机顺序
        
        public RandomIterator(){
            rank = size;
            iterArrays = (Item[]) new Object[rank];
            for(int i = 0; i<size; i++){
                iterArrays[i] = rqArrays[i];
            }
        }
        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return (rank > 0);
        }
        @Override
        public Item next() {
            // TODO Auto-generated method stub
            if (rank == 0)
                throw new NoSuchElementException("there are no more items!");
            int r = StdRandom.uniform(0, rank); // 随机选取一个位置的元素返回
            rank--;
            Item item = iterArrays[r];
            iterArrays[r] = iterArrays[rank];
            iterArrays[rank] = item; // 将已经遍历过的元素放置队列末尾，这样下次迭代就不会被选到
            return item;
        }
    }

    public RandomizedQueue() {
        // construct an empty randomized queue
        rqArrays = (Item[]) new Object[1];
        size = 0;
    }

    private void valivate(Item item) {
        if (item == null)
            throw new IllegalArgumentException("the item is null!");
    }

    public boolean isEmpty() {
        // is the queue empty?
        return (size == 0);
    }

    public int size() {
        // return the number of items on the queue
        return size;
    }

    private void resize(int cap) {
        Item[] temp = (Item[]) new Object[cap];
        for (int i = 0; i < size; i++)
            temp[i] = rqArrays[i];
        rqArrays = temp;
    }

    public void enqueue(Item item) {
        // add the item
        valivate(item);
        rqArrays[size++] = item;
        if (size == rqArrays.length)
            resize(2 * rqArrays.length);
    }

    public Item dequeue() {
        // remove and return a random item
        // 随机选取一个位置，将这个位置的元素与队列末尾的元素交换位置
        // dequeue末尾元素时就达到随机remove元素的目的
        if (size == 0)
            throw new NoSuchElementException("the RandomizeQueue is empty!");
        int r = StdRandom.uniform(0, size);
        size--;
        Item delItem = rqArrays[r];
        rqArrays[r] = rqArrays[size];
        rqArrays[size] = null;
        if (size > 0 && size == rqArrays.length / 4)
            resize(rqArrays.length / 2);
        return delItem;
    }

    public Item sample() {
        // return (but do not remove) a random item
        if (size == 0)
            throw new NoSuchElementException("the RandomizeQueue is empty!");
        return rqArrays[StdRandom.uniform(0, size)];
    }

    public Iterator<Item> iterator() {
        // return an independent iterator over items in random order
        return new RandomIterator();
    }

    public static void main(String[] args) {
        // unit testing (optional)
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        rq.enqueue("a");
        rq.enqueue("b");
        rq.enqueue("c");
        rq.enqueue("d");
        rq.enqueue("e");
        rq.enqueue("f");
        rq.enqueue("g");
        rq.dequeue();
        Iterator<String> iter1 = rq.iterator();
        Iterator<String> iter2 = rq.iterator();
        while (iter1.hasNext()) {
            System.out.print(iter1.next() + ",");
        }
        System.out.println();
        while (iter2.hasNext()) {
            System.out.print(iter2.next() + ",");
        }
        System.out.println();

    }
}
