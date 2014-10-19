package org.nights.npe.api;

import java.util.HashMap;

import lombok.Data;

@Data
public class ContextData {
	int procPIO = 0;// ;// Int = 0,
	int taskPIO = 0;// Int = 0,
	String rolemark;// String = null,
	long startMS = System.currentTimeMillis();
	Integer idata1;// Option[Int] = null, //integer ,
	Integer idata2;// Option[Int] = null, //integer,
	String strdata1;// String = null, //text,
	String strdata2;// String = null, //text,
	Float fdata1;// Option[Float] = null, //float,
	Float fdata2;// Option[Float] = null, //float,
	String taskcenter;// String = null,
	String rootproc;// String = null,
	HashMap<String, String> extra = new HashMap<>();// HashMap[String, Any] =
													// HashMap.empty
}
