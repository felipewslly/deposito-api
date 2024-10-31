package com.deposito.demo.services;


import com.deposito.demo.entities.Estoque;
import com.deposito.demo.entities.Produto;
import com.deposito.demo.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto criarProduto(Produto produto){
        if (produto.getNome() == null || produto.getNome().isEmpty()){
            throw new IllegalArgumentException("O NOME DO PRODUTO É OBRIGATÓRIO");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("O PREÇO DO PRODUTO DEVE SER POSITOVO");
        }

        if (produto.getEstoque().getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade inicial do produto não pode ser negativa.");
        }

        return produtoRepository.save(produto);
    }

    public String deletarProdutoPeloId(Long id){
        if (produtoRepository.existsById(id)){
            produtoRepository.deleteById(id);
            return "PRODUTO DELETADO COM SUCESSO! ";
        }

        return "PRODUTO COM ESTE ID NÃO ENCONTRADO";

    }

    public Produto acessarProdutoPeloId(Long id){
        return produtoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("PRODUTO COM O ID: " + id + " NÃO ENCONTRADO!"));
    }

    public Produto acessarProdutoPeloNome(String nomeDoProduto){
        return produtoRepository.findByName(nomeDoProduto).orElseThrow(
                ()-> new NoSuchElementException("PRODUTO COM O NOME: " + nomeDoProduto + " NÃO ENCONTRADO!"));

    }

    public Produto atualizarProdutoPeloId(Long id, Produto novosDadosProduto){

        Produto novoProduto = produtoRepository.findById(id).orElseThrow(
                ()-> new NoSuchElementException("PRODUTO COM ESTE ID: " + id + " NÃO EXISTE"));

        if (novosDadosProduto.getNome() == null || novosDadosProduto.getNome().isEmpty()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (novosDadosProduto.getPreco() == null || novosDadosProduto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser positivo.");
        }
        if (novosDadosProduto.getEstoque().getQuantidade() < 0) {
            throw new IllegalArgumentException("A quantidade do produto não pode ser negativa.");
        }

        Optional<Produto> produtoComMesmoNome = produtoRepository.findByName(novosDadosProduto.getNome());
        if (produtoComMesmoNome.isPresent() && !produtoComMesmoNome.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe um produto com esse nome.");
        }

        if (novosDadosProduto.getDataDeValidade() != null && novosDadosProduto.getDataDeValidade().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data de validade não pode ser no passado.");
        }

        novoProduto.setNome(novosDadosProduto.getNome());
        novoProduto.setPreco(novosDadosProduto.getPreco());
        novoProduto.setDescricao(novosDadosProduto.getDescricao());
        novoProduto.setDataDeValidade(novosDadosProduto.getDataDeValidade());

        return produtoRepository.save(novoProduto);

    }

    public void incrementarNoEstoque(Long produtoId, int quantidade) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("PRODUTO NÃO ENCONTRADO"));

        Estoque estoque = produto.getEstoque();
        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        produtoRepository.save(produto);

    }

    public void decrementarNoEstoque(Long produtoId, int quantidade){
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("PRODUTO NÃO ENCONTRADO"));

        Estoque estoque = produto.getEstoque();
            if (estoque.getQuantidade() >= quantidade){
                estoque.setQuantidade(estoque.getQuantidade() - quantidade);
                produtoRepository.save(produto);
            } else {
                throw new RuntimeException("ESTOQUE INSUFICIENTE PARA O PRODUTO: " + produto.getNome());
            }
    }

}
