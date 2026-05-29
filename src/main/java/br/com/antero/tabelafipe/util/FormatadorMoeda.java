package br.com.antero.tabelafipe.util;

import java.math.BigDecimal;

public class FormatadorMoeda {

    public static BigDecimal converterParaBigDecimal(String valorFipe) {
        if (valorFipe == null || valorFipe.isBlank()) {
            return BigDecimal.ZERO;
        }

        String valorLimpo = valorFipe.replace("R$", "").trim();

        valorLimpo = valorLimpo.replace(".", "");

        valorLimpo = valorLimpo.replace(",", ".");

        return new BigDecimal(valorLimpo);
    }
}
