package com.monocept.myapp.dto;

import lombok.Data;

@Data
public class CityRequestDto {
	private long id;
	private String name;
	private boolean active;

}