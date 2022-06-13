package search;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Sock{
	private DataInputStream sock_in;
	private DataOutputStream sock_out;
	
	public static ArrayList<BookDTO> searchedContents = new ArrayList<>();
	public static HashMap<String, byte[]> imageDataList = new HashMap<>();
	
	/*
		ㆍ[0] : 책 소개
		ㆍ[1] : 저자 소개
		ㆍ[2] : 목차
	*/
	public static HashMap<String, String[]> bookInfoList = new HashMap<>();
	
	
	public void search(String keyword) {
		searchedContents.clear();
		imageDataList.clear();
		bookInfoList.clear();
		
		try {
			sock_out.writeUTF("list_book");
			sock_out.writeUTF(keyword);
			sock_out.writeUTF("all_client");
			sock_out.flush();
			
			int searchedCount = sock_in.readInt();
			for(int i = 0; i < searchedCount; i++) {
				// ---------------------------------------------------------
				BookDTO dto = new BookDTO();
				dto.setId(sock_in.readUTF());
				dto.setTitle(sock_in.readUTF());
				dto.setWriter(sock_in.readUTF());
				dto.setPublisher(sock_in.readUTF());
				dto.setCategory(sock_in.readUTF());
				dto.setCurrent_quantity(sock_in.readInt());
				dto.setTotal_quantity(sock_in.readInt());
				
				searchedContents.add(dto);
				// ---------------------------------------------------------
				byte[] imageData = new byte[(int)sock_in.readLong()];
				sock_in.readFully(imageData);
				imageDataList.put(dto.getId(), imageData);
				// ---------------------------------------------------------
				byte[] bookInfoData = new byte[(int)sock_in.readLong()];
				sock_in.readFully(bookInfoData);
				String[] partition = new String(bookInfoData).split("ω");
				for(int j = 0; j < partition.length; j++) {
					partition[j] = partition[j].trim(); // 앞뒤 공백 제거
				}
				bookInfoList.put(dto.getId(), partition);
				// ---------------------------------------------------------
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int reservation(String reservationDate, String bookID) throws Exception {
		sock_out.writeUTF("insert_reservation");
		sock_out.writeUTF(reservationDate);
		sock_out.writeUTF(bookID);
		sock_out.flush();
		
		int result = sock_in.readInt();
		
		return result;
	}
	
	public int[] reservation(String bookID) throws Exception {
		int[] result = new int[2];
		
		sock_out.writeUTF("insert_standby");
		sock_out.writeUTF(bookID);
		sock_out.flush();
		
		result[0] = sock_in.readInt(); // 예약 결과
		result[1] = sock_in.readInt(); // 대기번호
		
		return result;
	}
	
	public int getOrderNum(String bookId) throws Exception {
		sock_out.writeUTF("get_orderNum");
		sock_out.writeUTF(bookId);
		
		return sock_in.readInt();
	}
	
	public void revalidateCurrentQuantity(String bookId) throws Exception {
		int current_quantity = sock_in.readInt();
		
		for(BookDTO dto : searchedContents) {
			if(dto.getId().equals(bookId)) {
				dto.setCurrent_quantity(current_quantity);
				break;
			}
		}
	}
	
	public Sock(DataInputStream sock_in, DataOutputStream sock_out) {
		try {
			this.sock_in = sock_in;
			this.sock_out = sock_out;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}