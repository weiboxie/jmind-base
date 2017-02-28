package jmind.base.collect;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 *   基于ConcurrentLinkedQueue定长队列
 * @author weibo-xie
 * 2012-4-24
 * @param <E>
 */
public class FixedConcurrentLinkedQueue<E>  extends ConcurrentLinkedQueue<E> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int capacity=Integer.MAX_VALUE ;
	private boolean add= true ;
	public FixedConcurrentLinkedQueue(int capacity){
		 this.capacity=capacity ;
	}
	public FixedConcurrentLinkedQueue(int capacity,boolean add){
		 this.capacity=capacity ;
		 this.add=add;
	}
	public boolean offer(E e){
       if(this.size()<capacity){
    	   return super.offer(e);
       }
       if(add){
    	   this.poll();
    	   return super.offer(e);
       }
       return false;
	 }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FixedConcurrentLinkedQueue<Integer> list=new FixedConcurrentLinkedQueue<Integer>(10);
		   for(int i=1;i<50;i++){
			   list.offer(i);
		   }
		   System.out.println(list);
		    Iterator<Integer> iterator = list.iterator() ;
		   for(int i=100;i<200;i++){
			   list.offer(i);
		   }
		   for(Integer i:list){
			   System.out.println(i);
		   }
		   System.out.println(iterator.next());

	}

}
