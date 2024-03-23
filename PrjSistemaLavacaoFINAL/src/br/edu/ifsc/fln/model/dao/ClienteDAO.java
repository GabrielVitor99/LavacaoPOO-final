package br.edu.ifsc.fln.model.dao;

import br.edu.ifsc.fln.model.domain.Cliente;
import br.edu.ifsc.fln.model.domain.PessoaFisica;
import br.edu.ifsc.fln.model.domain.PessoaJuridica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gabrielvitor  
 */
public class ClienteDAO {
    
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public boolean inserir(Cliente cliente) throws SQLException{
        String sql = "INSERT INTO cliente(nome, telefone, email, data_cadastro) VALUES(?, ?, ?, ?)";
        String sqlPF = "INSERT INTO pessoafisica(id_cliente, cpf, data_nascimento) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        String sqlPJ = "INSERT INTO pessoajuridica(id_cliente, cnpj, inscricao_estadual) VALUES((SELECT max(id) FROM cliente), ?, ?)";
        
        //armazena os dados da superclasse
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getTelefone());
        stmt.setString(3, cliente.getEmail());
        stmt.setDate(4, java.sql.Date.valueOf(cliente.getDataCadastro()));
        stmt.execute();
        
        //armazena os dados da subclasse
        if (cliente instanceof PessoaFisica) {
            stmt = connection.prepareStatement(sqlPF);
            stmt.setString(1, ((PessoaFisica)cliente).getCpf());
            stmt.setDate(2, java.sql.Date.valueOf(((PessoaFisica)cliente).getDataNascimento()));
            stmt.execute();
        } else {
            stmt = connection.prepareStatement(sqlPJ);
            stmt.setString(1, ((PessoaJuridica)cliente).getCnpj());
            stmt.setString(2, ((PessoaJuridica)cliente).getInscricaoEstadual());
            stmt.execute();
        }
        return false;
    }
    
    
     public boolean alterar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome=?, email=?, telefone=?, data_cadastro=? WHERE id=?";
        String sqlPF = "UPDATE pessoaFisica SET cpf=?, dataNascimento=? WHERE id_cliente=?";
        String sqlPJ = "UPDATE pessoaJuridica SET cnpj=?, inscricao_estadual=? WHERE id_cliente=?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getTelefone());
            //stmt.setString(4, cliente.getEndereco());
            stmt.setDate(4, java.sql.Date.valueOf(cliente.getDataCadastro()));
            stmt.setInt(5, cliente.getId());
            stmt.execute();
            if (cliente instanceof PessoaFisica) {
                PreparedStatement stmtPF = connection.prepareStatement(sqlPF);
                stmtPF.setString(1, ((PessoaFisica)cliente).getCpf());
                stmtPF.setDate(2, java.sql.Date.valueOf(((PessoaFisica) cliente).getDataNascimento()));
                stmtPF.setInt(3, cliente.getId());
                stmt.execute();
            } else if (cliente instanceof PessoaJuridica) {
                PreparedStatement stmtPJ = connection.prepareStatement(sqlPJ);
                stmtPJ.setString(1, ((PessoaJuridica)cliente).getCnpj());
                stmtPJ.setString(2, ((PessoaJuridica)cliente).getInscricaoEstadual());
                stmtPJ.setInt(3, cliente.getId());
                stmtPJ.execute();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    
    public boolean remover(Cliente cliente) throws SQLException{
        String sql = "DELETE FROM cliente WHERE id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, cliente.getId());
        stmt.execute();
        return false;
    }
    
    
  public List<Cliente> listar() {
        String sql = "SELECT c.id, c.nome, c.telefone, c.email, c.data_cadastro, j.cnpj, j.inscricao_estadual, f.cpf, f.data_nascimento "
                        + "FROM cliente c "
                        + "LEFT JOIN pessoaFisica f on c.id = f.id_cliente "
                        + "LEFT JOIN pessoaJuridica j on  c.id = j.id_cliente ";
        List<Cliente> retorno = new ArrayList<>();
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet resultado = stmt.executeQuery();
            while (resultado.next()) {
                Cliente cliente = populateVO(resultado);
                retorno.add(cliente);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retorno;
    }
    
    
    public Cliente buscar(Cliente cliente) throws SQLException{
        String sql = "SELECT * FROM cliente c "
                        + "LEFT JOIN pessoafisica pf on pf.id_cliente = c.id "
                        + "LEFT JOIN pessoajuridica pj on pj.id_cliente = c.id WHERE id=?";
        Cliente retorno = null;
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, cliente.getId());
        ResultSet resultado = stmt.executeQuery();
        if (resultado.next()) {
            retorno = populateVO(resultado);
        }
        return retorno;
    }
    
    
    public Cliente buscar(int id) throws SQLException {
    String sql = "SELECT * FROM cliente c "
                + "LEFT JOIN pessoafisica pf on pf.id_cliente = c.id "
                + "LEFT JOIN pessoajuridica pj on pj.id_cliente = c.id WHERE id=?";
    Cliente retorno = null;
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet resultado = stmt.executeQuery();
        if (resultado.next()) {
            retorno = populateVO(resultado);
        }
    return retorno;
    } 
    
    
      private Cliente populateVO(ResultSet resultado) throws SQLException {
        Cliente cliente;
        String cnpj = resultado.getString("cnpj");
        String inscricao_estadual = resultado.getString("inscricao_estadual");
        String cpf = resultado.getString("cpf");
        
        if (cnpj != null & inscricao_estadual != null){
            cliente = new PessoaJuridica();
            ((PessoaJuridica)cliente).setCnpj(cnpj);
            ((PessoaJuridica)cliente).setInscricaoEstadual(inscricao_estadual);
        }
        else {
            cliente = new PessoaFisica();
            ((PessoaFisica)cliente).setCpf(cpf);
            ((PessoaFisica)cliente).setDataNascimento(resultado.getDate("data_nascimento").toLocalDate());
            System.out.println();
        }
        
        cliente.setId(resultado.getInt("id"));
        cliente.setNome(resultado.getString("nome"));
        cliente.setTelefone(resultado.getString("telefone"));
        cliente.setEmail(resultado.getString("email"));
        cliente.setDataCadastro(resultado.getDate("data_cadastro").toLocalDate());
        
        
        return cliente;
    }  
    
    
    public int getClienteAutoID(Cliente cliente) throws SQLException{
    String sql1= "SELECT max(id) as id FROM cliente";
    int id = 0;
        PreparedStatement stmt = connection.prepareStatement(sql1);
        ResultSet resultado = stmt.executeQuery();
        while (resultado.next()) {
        id = resultado.getInt("id");
        }
    return id;
    }
    
    
    
    
}
