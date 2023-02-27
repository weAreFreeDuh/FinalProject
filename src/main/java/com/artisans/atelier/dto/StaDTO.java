package com.artisans.atelier.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@Data
@Alias("statistics")
public class StaDTO {

	private String staName; // 통계 x축 제목
	private int staNum;	// 통계 숫자
	private int staNum2;	// 추가 통계숫자
	private int staNum3;	// 추가 통계숫자
	private int staNum4;	// 추가 통계숫자
	private int staNum5;	// 추가 통계숫자
	private int staNum6;	// 추가 통계숫자
	private int staNum7;	// 추가 통계숫자
	private int staNum8;	// 추가 통계숫자
	private int staNum9;	// 추가 통계숫자

	@DateTimeFormat(pattern = "yyyy-MM")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM", timezone = "Asia/Seoul")
	private YearMonth yearMonth;
}
