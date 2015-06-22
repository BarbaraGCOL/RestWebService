/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import entidades.Estoque;
import entidades.Produto;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author BárbaraGabrielle
 */
@Stateless
@Path("produto")
public class ProdutoFacadeREST extends AbstractFacade<Produto> {
    @PersistenceContext(unitName = "RestWSProdPU")
    private EntityManager em;

    @EJB
    private EstoqueFacadeREST estoqueFacadeREST;
    
    public ProdutoFacadeREST() {
        super(Produto.class);
    }

    @POST
    @Path("add")
    @Consumes({"application/xml", "application/json"})
    @Produces({"application/xml", "application/json"})
    public List<Produto> adicionar(Produto entity) {
        super.create(entity);
        return super.findAll();
    }

    @PUT
    @Path("addEstoque/{codigoProduto}/{quantidade}")
    @Consumes({"application/xml", "application/json"})
    @Produces("text/plain")
    public String adicionarEstoque(@PathParam("codigoProduto") Integer codigoProduto, @PathParam("quantidade") Integer qt) {
        Produto p = super.find(codigoProduto);
        if(p != null){
            Estoque e = new Estoque();
            
            if(p.getEstoque() != null){
                int q = p.getEstoque().getQuantidade();
                e.setQuantidade(q + qt);
                e.setId(p.getEstoque().getId());
            }
            else{
                e.setQuantidade(qt);
            }
            e.setProduto(super.find(codigoProduto));
            estoqueFacadeREST.edit(e);
            List etqs = estoqueFacadeREST.findAll();
            Estoque saved = (Estoque)etqs.get(etqs.size()-1);
            p.setEstoque(saved);
            super.edit(p);
            return "Estoque adicionado com sucesso!";
        }
        return "Produto não existente!";
    }
    
    @GET
    @Path("estoque/{codigoProduto}")
    @Produces({"application/xml", "application/json"})
    public Estoque findEstoque(@PathParam("codigoProduto") Integer codigoProduto) {
        Produto p = super.find(codigoProduto);
        if(p.getEstoque() == null || p.getEstoque().getQuantidade() == null){
            return null;
        }
        return p.getEstoque();
    }
    
    @DELETE
    @Path("removerEstoque/{codigoProduto}/{quantidade}")
    @Produces("text/plain")
    public String removeEstoque(@PathParam("codigoProduto") Integer codigoProduto, @PathParam("quantidade") Integer qt) {
        Produto p = super.find(codigoProduto);
        if(p.getEstoque()!=null){
            Estoque e = p.getEstoque();
            int q = p.getEstoque().getQuantidade();
            int qtFinal = q - qt;
            if(qtFinal < 0){
                qtFinal = 0;
            }
            e.setQuantidade(qtFinal);
            estoqueFacadeREST.edit(e);
            p.setEstoque(e);
            super.edit(p);
            return "Estoque do produto removido com sucesso!";
        }
        return "Estoque não encontrado!";
    }
    
    @GET
    @Path("{codigo}")
    @Produces({"application/xml", "application/json"})
    public Produto find(@PathParam("codigo") Integer codigo) {
        return super.find(codigo);
    }

    @GET
    @Override
    @Path("listar")
    @Produces({"application/xml", "application/json"})
    public List<Produto> findAll() {
        return super.findAll();
    }
    
//    @PUT
//    @Path("{codigo}")
//    @Consumes({"application/xml", "application/json"})
//    public void edit(@PathParam("codigo") Integer codigo, Produto entity) {
//        super.edit(entity);
//    }
//
//    @DELETE
//    @Path("{codigo}")
//    public void remove(@PathParam("codigo") Integer codigo) {
//        super.remove(super.find(codigo));
//    }

//    @GET
//    @Path("count")
//    @Produces("text/plain")
//    public String countREST() {
//        return String.valueOf(super.count())+" produtos cadastrados";
//    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
