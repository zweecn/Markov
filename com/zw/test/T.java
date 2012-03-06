package com.zw.test;

public class T {
	int a;
	int b;
	int c;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + a;
		result = prime * result + b;
		result = prime * result + c;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof T)) {
			return false;
		}
		T other = (T) obj;
		if (a != other.a) {
			return false;
		}
		if (b != other.b) {
			return false;
		}
		if (c != other.c) {
			return false;
		}
		return true;
	}
}
