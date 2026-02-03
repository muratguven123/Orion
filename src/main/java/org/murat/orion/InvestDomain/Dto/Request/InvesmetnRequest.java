package org.murat.orion.InvestDomain.Dto.Request;

import lombok.Data;
import org.murat.orion.InvestDomain.Entity.InvestType;
import org.murat.orion.InvestDomain.Entity.İnvestType;

import java.math.BigDecimal;

@Data

public class InvesmetnRequest{
private Long userId;
private String symbol;
private BigDecimal quantity;
private InvestType type;
}