package org.nights.npe.api;

import lombok.Data;

@Data
public class ObtainedStates {
	StateContext state;
	ContextData ctxData;
	Obtainer obtainer;
}
