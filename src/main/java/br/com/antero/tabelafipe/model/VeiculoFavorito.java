package br.com.antero.tabelafipe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "veiculos_favoritos")
public class VeiculoFavorito {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // é uma excelente prática de performance; ele diz ao banco de dados: "Quando você buscar o carro favorito, não carregue todos os dados pesados do usuário de forma automática, a menos que eu peça expressamente".
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String codigoMarca;

    @Column(nullable = false)
    private String codigoModelo;

    @Column(nullable = false)
    private String codigoAno;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String ano;

    @Column(nullable = false)
    private String valorSalvo;

    @Column(nullable = false)
    private String tipoVeiculo;

    public VeiculoFavorito() {
    }

    public VeiculoFavorito(Usuario usuario, String tipoVeiculo, String codigoMarca, String codigoModelo, String codigoAno,
                           String marca, String modelo, String ano, String valorSalvo) {
        this.usuario = usuario;
        this.tipoVeiculo = tipoVeiculo;
        this.codigoMarca = codigoMarca;
        this.codigoModelo = codigoModelo;
        this.codigoAno = codigoAno;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.valorSalvo = valorSalvo;
    }

    public UUID getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }

    public String getCodigoMarca() { return codigoMarca; }
    public void setCodigoMarca(String codigoMarca) { this.codigoMarca = codigoMarca; }

    public String getCodigoModelo() { return codigoModelo; }
    public void setCodigoModelo(String codigoModelo) { this.codigoModelo = codigoModelo; }

    public String getCodigoAno() { return codigoAno; }
    public void setCodigoAno(String codigoAno) { this.codigoAno = codigoAno; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getAno() { return ano; }
    public void setAno(String ano) { this.ano = ano; }

    public String getValorSalvo() { return valorSalvo; }
    public void setValorSalvo(String valorSalvo) { this.valorSalvo = valorSalvo; }
}
