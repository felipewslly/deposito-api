package com.deposito.demo.services;


import com.deposito.demo.entities.Fornecedor;
import com.deposito.demo.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class FornecedorService{

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }


    public Fornecedor criarFornecedor(Fornecedor fornecedor){
        if (fornecedor.getNome() == null || fornecedor.getNome().isEmpty()){
            throw new IllegalArgumentException("O NOME DEVE SER PREENCHIDO CORRETAMENTE!");
        }

        if (fornecedor.getTelefone() == null || fornecedor.getEmail().isEmpty()){
            throw new IllegalArgumentException("O EMAIL DEVE SER PREENCHIDO CORRETAMENTE! ");
        }

        return fornecedorRepository.save(fornecedor);
    }

    public String deletarFornecedor(Long id){
        if (fornecedorRepository.existsById(id)){
            fornecedorRepository.deleteById(id);
            return "PRODUTO DELETADO COM SUCESSO!";
        }

            return "PRODUTO COM ESTE ID NÃO ENCONTRADO";

    }

    public Fornecedor fornecedorPeloNome(String nomeDoFornecedor){
        return fornecedorRepository.findFornecedorByName(nomeDoFornecedor).orElseThrow(
                () -> new NoSuchElementException("NOME DESTE FORNECEDOR NÃO ENCONTRADO"));
    }
}
