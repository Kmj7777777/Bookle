package search;

import java.util.ArrayList;

public class Paging {
	private int searchedCount;
	private ArrayList<BookDTO> currentPageContents = new ArrayList<>();
	
	private int currentPage; // 현재 페이지 번호
	private int startPage; // 현재 페이지를 기준으로 한 시작 페이지의 번호
	private int endPage; // 현재 페이지를 기준으로 한 마지막 페이지의 번호
	private int totalPages; // 총 페이지 개수
	
	
	public Paging(int searchedCount, int currentPage, int size) { // size : 페이지당 콘텐츠 개수
		this.searchedCount = searchedCount;
		this.currentPage = currentPage;
		
		if(0 == searchedCount) {
			startPage = 0;
			endPage = 0;
			totalPages = 0;
		}else {
			totalPages = searchedCount / size;
			if(searchedCount % size > 0) totalPages++;
			
			int modVal = currentPage % 5;
			startPage = currentPage / 5 * 5 + 1;
			
			if(0 == modVal) startPage -= 5; // 현재 페이지가 5의 배수(마지막 페이지)일 경우, 시작 페이지가 잘못 설정되는 것을 방지
			
			endPage = startPage + 4;
			if(endPage > totalPages) endPage = totalPages;
			
			// 현재 페이지에 해당하는 콘텐츠 구하기
			if(currentPage == endPage) {
				for(int i = currentPage * size - (size - 1);   i <= currentPage * size;   i++) {
					if(Sock.searchedContents.size() < i) break; // 마지막 페이지는 콘텐츠의 개수가 size보다 적을 수 있으므로.(IndexOutOfBounds 예외 방지)
					currentPageContents.add(Sock.searchedContents.get(i - 1)); // ArrayList는 index값이 0부터 시작하므로 -1을 해주어야 한다.
				}
			}else {
				for(int i = currentPage * size - (size - 1);   i <= currentPage * size;   i++) {
					currentPageContents.add(Sock.searchedContents.get(i - 1));
				}
			}
		}
	}
	
	public boolean hasNoResult() {
		return 0 == searchedCount;
	}

	public boolean hasResult() {
		return searchedCount > 0;
	}
	
	public ArrayList<BookDTO> getCurrentPageContents(){
		return currentPageContents;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	
	public int getStartPage() {
		return startPage;
	}
	
	public int getEndPage() {
		return endPage;
	}

	public int getTotalPages() {
		return totalPages;
	}
}