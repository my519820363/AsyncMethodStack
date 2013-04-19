package com.huyunfeng.asyncmethodstack;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Stack<T> {
	private LinkedList<T> storage = new LinkedList<T>();

	/**
	 * 加入元素到栈顶
	 * @param v
	 */
	public synchronized void push(T v) {
		storage.addFirst(v);
	}

	/**
	 * 获取栈顶元素
	 * @return
	 */
	public synchronized T peek() {
		try {
			return storage.getFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * 获取栈顶元素，并将它从栈中删除
	 * @return
	 */
	public synchronized T pop() {
		try {
			return storage.removeFirst();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * 栈是否为空
	 * @return
	 */
	public synchronized boolean empty() {
		return storage.isEmpty();
	}

	public synchronized String toString() {
		return storage.toString();
	}
}
