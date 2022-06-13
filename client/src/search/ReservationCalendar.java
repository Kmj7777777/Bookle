package search;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.freixas.jcalendar.JCalendar;

public class ReservationCalendar extends JDialog{
	private JTextField reservation;
	private Calendar serverDate = Calendar.getInstance();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	// ----------------------------------------------------------------------------------------------
	private JCalendar cal = new JCalendar();
	private JButton confirmBtn = new JButton("완료");
	private JPanel combinePanel = new JPanel(new FlowLayout()); // cal과 confirmBtn을 합쳐주는 Panel
	

	private void compInit() {
		cal.setDate(new Date(GetServerTime.serverTime)); // 서버의 현재 날짜가 표시되게 하기 위해 날짜를 다시 설정
		serverDate.setTime(new Date(GetServerTime.serverTime));
		
		confirmBtn.setPreferredSize(new Dimension(70, 40));
		combinePanel.add(cal);
		combinePanel.add(confirmBtn);
		
		this.add(combinePanel);
	}
	
	private void eventInit() {
		confirmBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Date convertedServerDate = setToTimeZero(serverDate); // 날짜값만 비교하기 위해 시간값을 0으로 설정
				
				Calendar selectedDate = cal.getCalendar();
				Date convertedSelectedDate = setToTimeZero(cal.getCalendar());

				if(convertedSelectedDate.compareTo(convertedServerDate) < 0) { // 과거
					JOptionPane.showMessageDialog(ReservationCalendar.this, "잘못된 선택입니다.", "", JOptionPane.ERROR_MESSAGE);
				}else if(convertedSelectedDate.compareTo(convertedServerDate) > 0) { // 미래
					Calendar temp = Calendar.getInstance();
					temp.setTime(convertedServerDate);
					temp.add(Calendar.DATE, 3);
					Date reservationPossiblePeriod = new Date(temp.getTimeInMillis());
					if(convertedSelectedDate.compareTo(reservationPossiblePeriod) <= 0) { // 서버의 현재 날짜로부터 3일 이후까지만 예약 가능
						reservation.setText(sdf.format(new Date(selectedDate.getTimeInMillis())));
						dispose();
					}else {
						int month = temp.get(Calendar.MONTH) + 1; // 0 : 1월
						int days = temp.get(Calendar.DATE);
						JOptionPane.showMessageDialog(ReservationCalendar.this,
																	   "　　장기간 예약은 불가능합니다.\n(최대 " + month + "월 " + days + "일까지 예약이 가능합니다.)",
																	   "", JOptionPane.ERROR_MESSAGE);
					}
				}else if(convertedSelectedDate.compareTo(convertedServerDate) == 0){ // 현재
					reservation.setText(sdf.format(new Date(selectedDate.getTimeInMillis())));
					dispose();
				}
			}
		});
	}
	
	/*
 		시간값을 0으로 설정해주는 메서드
 		ex) 2018년 1월 20일 11시 30분 30초  →  2018년 1월 20일 00시 00분 00초
	*/
	private Date setToTimeZero(Calendar cal) {
		Date date = null;
		
		try {
			date = sdf.parse(cal.get(Calendar.YEAR) + "/" +
								  (cal.get(Calendar.MONTH) + 1) + "/" +
								   cal.get(Calendar.DATE));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return date;
	}
	
	public ReservationCalendar(JTextField reservation) {
		this.reservation = reservation; 
		
		this.setSize(350, 340);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		compInit();
		eventInit();
		
		this.setVisible(true);
	}
}