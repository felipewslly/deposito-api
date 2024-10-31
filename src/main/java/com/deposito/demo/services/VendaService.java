package com.deposito.demo.services;


import com.deposito.demo.entities.ItemVenda;
import com.deposito.demo.entities.Venda;
import com.deposito.demo.repository.ItemVendaRepository;
import com.deposito.demo.repository.VendaRepository;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VendaService {
    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;

    public VendaService(VendaRepository vendaRepository, ItemVendaRepository itemVendaRepository) {
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
    }

    public Venda realizarVenda(List<ItemVenda> itensVenda) {
        Venda venda = new Venda();
        venda.setData(LocalDate.now());


        double total = itensVenda.stream()
                .mapToDouble(item -> item.getQuantidade() * item.getPreco())
                .sum();
        venda.setTotal(total);


        itensVenda.forEach(item -> {
            item.setVenda(venda);
            itemVendaRepository.save(item);
        });

        venda.setItensVenda(itensVenda);

        vendaRepository.save(venda);

        return venda;

    }

    public Venda atualizarVenda(Long vendaId, List<ItemVenda> novosItensVenda){
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("VENDA NÃO ENCONTRADA"));

        venda.getItensVenda().clear();
        venda.getItensVenda().addAll(novosItensVenda);
        venda.getItensVenda().forEach(item -> item.setVenda(venda));

        vendaRepository.save(venda);
        return venda;

    }


    public Venda aplicarDesconto(Long vendaId, double percentual){
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("VENDA NÃO ENCONTRADA"));

        double desconto = venda.getTotal() * (percentual / 100);
        venda.setTotal(venda.getTotal() - desconto);
        return vendaRepository.save(venda);
    }

    public Venda consultarVendaPorId(Long vendaId) {
        return vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    public List<Venda> listarTodasVendas() {
        return vendaRepository.findAll();
    }



}
