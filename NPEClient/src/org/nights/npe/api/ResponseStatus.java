package org.nights.npe.api;

import lombok.Data;

@Data
public class ResponseStatus {

	Integer status;
	String msg;
	String faileReason;
}
