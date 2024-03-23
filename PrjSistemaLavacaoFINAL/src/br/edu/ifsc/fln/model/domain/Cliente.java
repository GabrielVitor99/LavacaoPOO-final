/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.edu.ifsc.fln.model.domain;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author gabrielvitor
 */
public abstract class Cliente implements ICliente{
    
    /*atributos*/
    private int id; 
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataCadastro;
    private List<Veiculo> veiculo;
    private Pontuacao pontuacao;

    
    private void createPontuacao() {
        this.pontuacao = new Pontuacao();
        this.pontuacao.setCliente(this);
    }
    
    public Cliente() {
        this.createPontuacao();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public List<Veiculo> getVeiculo() {
        return this.veiculo;
    }

    public Pontuacao getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Pontuacao pontuacao) {
        this.pontuacao = pontuacao;
    }

    @Override
    public String toString() {
        return  nome;
    }


    
    
}
