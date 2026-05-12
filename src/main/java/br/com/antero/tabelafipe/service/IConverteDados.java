package br.com.antero.tabelafipe.service;

public interface IConverteDados {
    <T> T obterDados (String json, Class<T> classe);
}
