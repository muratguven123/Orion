package org.murat.orion.InvestDomain.İnterface;

import org.murat.orion.InvestDomain.Entity.Invesment;
import org.murat.orion.InvestDomain.Entity.InvestType;


public interface InvesmentStrategy {
    InvestType getInvestType();
    void validExecute(String symbol);
}
