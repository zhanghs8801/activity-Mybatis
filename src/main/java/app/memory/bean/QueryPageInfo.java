package app.memory.bean;

import java.util.ArrayList;

public class QueryPageInfo<T> extends ArrayList<T>{
	private static final long serialVersionUID = -7711765939123046279L;
	private int currentPage;
	private int pageSize;
	private int startIndex;
	private long totalCount;
	private int totalPage;

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartIndex() {
		this.startIndex = (this.currentPage - 1) * this.pageSize;
		if (startIndex < 0) {
			startIndex = 0;
		}
		return startIndex;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
	public int getTotalPage() {
		return totalPage;
	}
	
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}