package com.github.jitpack.smappeelocalmqtt.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class MeasurementsDTO {
	private BigDecimal voltage;
	private BigDecimal phase1_current;
	private Integer phase1_activePower;
	private Integer phase1_reactivePower;
	private Integer phase1_apparentPower;
	private Integer phase1_cosfi;
	private BigDecimal phase2_current;
	private Integer phase2_activePower;
	private Integer phase2_reactivePower;
	private Integer phase2_apparentPower;
	private Integer phase2_cosfi;
	private BigDecimal phase3_current;
	private Integer phase3_activePower;
	private Integer phase3_reactivePower;
	private Integer phase3_apparentPower;
	private Integer phase3_cosfi;

	private BigDecimal total_current;
	private Integer total_activePower;
	private Integer total_reactivePower;
	private Integer total_apparentPower;
}
