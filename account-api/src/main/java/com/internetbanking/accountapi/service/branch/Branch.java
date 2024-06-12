package com.internetbanking.accountapi.service.branch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Branch {

    @JsonProperty("CnpjBase")
    private String cnpjBase;

    @JsonProperty("CnpjSequencial")
    private String cnpjSequencial;

    @JsonProperty("CnpjDv")
    private String cnpjDv;

    @JsonProperty("NomeIf")
    private String nomeIf;

    @JsonProperty("Segmento")
    private String segmento;

    @JsonProperty("CodigoCompe")
    private String codigoCompe;

    @JsonProperty("NomeAgencia")
    private String nomeAgencia;

    @JsonProperty("Endereco")
    private String endereco;

    @JsonProperty("Numero")
    private String numero;

    @JsonProperty("Complemento")
    private String complemento;

    @JsonProperty("Bairro")
    private String bairro;

    @JsonProperty("Cep")
    private String cep;

    @JsonProperty("MunicipioIbge")
    private String municipioIbge;

    @JsonProperty("Municipio")
    private String municipio;

    @JsonProperty("UF")
    private String uf;

    @JsonProperty("DataInicio")
    private String dataInicio;

    @JsonProperty("DDD")
    private String ddd;

    @JsonProperty("Telefone")
    private String telefone;

    @JsonProperty("Posicao")
    private String posicao;
}
