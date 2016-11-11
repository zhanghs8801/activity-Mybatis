package app.memory.bean;

import java.util.List;

public class QueryPageInfo {
	private int currentPage;
	private int pageSize;
	private int startIndex;
	private long totalCount;
	private List<Object> results;

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

	public List<Object> getResults() {
		return results;
	}

	public void setResults(List<Object> results) {
		this.results = results;
	}
}
