create database Loja;
use Loja;
create table Produto (codigo int not null auto_increment, descricao varchar(100), 
preco float, categoria varchar(20), primary key (codigo));
create table Estoque (id int not null auto_increment, codigo_produto int, quantidade int, 
primary key (id), FOREIGN KEY (codigo_produto) references Produto (codigo));
