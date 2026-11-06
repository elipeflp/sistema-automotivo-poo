"use strict";
const $=s=>document.querySelector(s), $$=s=>[...document.querySelectorAll(s)];
const labels={DISPONIVEL:"Disponível",RESERVADO:"Reservado",VENDIDO:"Vendido",DESCONTINUADO:"Descontinuado",CARRO:"Carro",MOTO:"Moto",UTILITARIO:"Utilitário"};
let marcas=[],modelos=[],veiculos=[];
const dinheiro=v=>Number(v).toLocaleString("pt-BR",{style:"currency",currency:"BRL"});
const esc=s=>String(s).replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
function avisar(msg,erro=false){const n=$("#mensagem");n.textContent=msg;n.className=erro?"erro":"";n.hidden=false;clearTimeout(avisar.timer);avisar.timer=setTimeout(()=>n.hidden=true,8000);}
async function api(path,method="GET",data){const r=await fetch("/api"+path,{method,headers:{"Content-Type":"application/json"},...(data?{body:JSON.stringify(data)}:{})});if(!r.ok){let e;try{e=await r.json()}catch{e={mensagem:"Não foi possível concluir a operação."}}throw Error([e.mensagem,...(e.detalhes||[])].join(" "));}return r.status===204?null:r.json();}
function executar(fn){return async e=>{if(e)e.preventDefault();try{await fn(e)}catch(err){avisar(err.message,true)}};}
function opcoes(sel,items,placeholder){const old=sel.value;sel.innerHTML=`<option value="">${placeholder}</option>`+items.map(x=>`<option value="${x.id}">${esc(x.nome)}</option>`).join("");sel.value=items.some(x=>String(x.id)===old)?old:"";}
function modelosPara(marcaId){return modelos.filter(x=>!marcaId||String(x.marcaId)===String(marcaId));}
function atualizarModelosFormulario(){opcoes($("#vModelo"),$("#vMarca").value?modelosPara($("#vMarca").value):[],"Selecione o modelo");}
function atualizarModelosFiltro(){opcoes($("#fModelo"),modelosPara($("#fMarca").value),"Todos os modelos");}
async function carregarCatalogo(){[marcas,modelos]=await Promise.all([api("/marcas"),api("/modelos")]);opcoes($("#fMarca"),marcas,"Todas as marcas");opcoes($("#vMarca"),marcas,"Selecione a marca");opcoes($("#modeloMarca"),marcas,"Selecione a marca");atualizarModelosFiltro();atualizarModelosFormulario();
 $("#marcasLista").innerHTML=marcas.map(m=>`<li><strong>${esc(m.nome)}</strong><div class="row-actions"><button data-editar-marca="${m.id}">Editar</button><button class="danger" data-excluir-marca="${m.id}">Excluir</button></div></li>`).join("")||"<li>Nenhuma marca cadastrada.</li>";
 $("#modelosLista").innerHTML=modelos.map(m=>`<li><div><strong>${esc(m.nome)}</strong><small>${esc(m.marca)}</small></div><div class="row-actions"><button data-editar-modelo="${m.id}">Editar</button><button class="danger" data-excluir-modelo="${m.id}">Excluir</button></div></li>`).join("")||"<li>Nenhum modelo cadastrado.</li>";
}
let consultaAtual=0;
async function carregarVeiculos(){const consulta=++consultaAtual;const params=new URLSearchParams();for(const[k,v]of new FormData($("#filtros")))if(v!=="")params.set(k,v);const resultado=await api("/veiculos?"+params);if(consulta!==consultaAtual)return;veiculos=resultado;
 const disponiveis=veiculos.filter(v=>v.status==="DISPONIVEL");$("#total").textContent=veiculos.length;$("#disponiveis").textContent=disponiveis.length;$("#valor").textContent=dinheiro(disponiveis.reduce((s,v)=>s+Number(v.preco),0));$("#contagem").textContent=`${veiculos.length} registro(s)`;$("#vazio").hidden=veiculos.length!==0;
 $("#veiculos").innerHTML=veiculos.map(v=>`<tr><td><strong>${esc(v.marca)} ${esc(v.modelo)}</strong><small>#${v.id} · ${labels[v.tipo]}</small></td><td>${v.ano}<small>${esc(v.cor)}</small></td><td>${Number(v.quilometragem).toLocaleString("pt-BR")} km</td><td><strong>${dinheiro(v.preco)}</strong></td><td><span class="badge ${v.status}">${labels[v.status]}</span></td><td><div class="row-actions"><button data-editar="${v.id}">Editar</button><button class="danger" data-excluir="${v.id}" ${["VENDIDO","DESCONTINUADO"].includes(v.status)?"":'disabled title="Marque como vendido ou descontinuado para excluir"'}>Excluir</button></div></td></tr>`).join("");}
function abrirVeiculo(v){if(!modelos.length){avisar("Cadastre uma marca e um modelo antes do veículo.",true);mudarAba(true);return;}const f=$("#formVeiculo");f.reset();f.elements.id.value="";$("#tituloVeiculo").textContent=v?"Editar veículo":"Cadastrar veículo";$("#vMarca").value=v?v.marcaId:"";atualizarModelosFormulario();if(v)for(const[k,val]of Object.entries(v))if(f.elements.namedItem(k))f.elements.namedItem(k).value=val;f.elements.ano.max=new Date().getFullYear()+1;$("#dialogVeiculo").showModal();}
function mudarAba(catalogo){$("#catalogo").hidden=!catalogo;$("#estoque").hidden=catalogo;$("#tabEstoque").classList.toggle("active",!catalogo);$("#tabCatalogo").classList.toggle("active",catalogo);}
$("#tabEstoque").onclick=()=>mudarAba(false);$("#tabCatalogo").onclick=()=>mudarAba(true);
$("#novoVeiculo").onclick=()=>abrirVeiculo();$("#fecharDialog").onclick=$("#cancelarVeiculo").onclick=()=>$("#dialogVeiculo").close();
$("#vMarca").onchange=atualizarModelosFormulario;$("#fMarca").onchange=atualizarModelosFiltro;
$("#filtros").onsubmit=executar(carregarVeiculos);$("#filtros").onreset=()=>setTimeout(executar(async()=>{atualizarModelosFiltro();await carregarVeiculos()}),0);
$("#formVeiculo").onsubmit=executar(async()=>{const f=$("#formVeiculo"),d=Object.fromEntries(new FormData(f)),id=d.id;delete d.id;for(const k of ["modeloId","ano","preco","quilometragem"])d[k]=Number(d[k]);await api("/veiculos"+(id?"/"+id:""),id?"PUT":"POST",d);$("#dialogVeiculo").close();await carregarVeiculos();avisar(id?"Veículo atualizado com sucesso.":"Veículo cadastrado com sucesso.");});
$("#formMarca").onsubmit=executar(async()=>{const f=$("#formMarca"),d=Object.fromEntries(new FormData(f)),id=d.id;await api("/marcas"+(id?"/"+id:""),id?"PUT":"POST",{nome:d.nome});f.reset();f.elements.id.value="";await carregarCatalogo();await carregarVeiculos();avisar("Marca salva com sucesso.");});
$("#formModelo").onsubmit=executar(async()=>{const f=$("#formModelo"),d=Object.fromEntries(new FormData(f)),id=d.id;await api("/modelos"+(id?"/"+id:""),id?"PUT":"POST",{nome:d.nome,marcaId:Number(d.marcaId)});f.reset();f.elements.id.value="";await carregarCatalogo();await carregarVeiculos();avisar("Modelo salvo com sucesso.");});
$("#cancelarMarca").onclick=()=>{$("#formMarca").reset();$("#formMarca").elements.id.value=""};$("#cancelarModelo").onclick=()=>{$("#formModelo").reset();$("#formModelo").elements.id.value=""};
document.addEventListener("click",async e=>{try{const b=e.target.closest("button");if(!b)return;const d=b.dataset;
 if(d.editar)abrirVeiculo(veiculos.find(v=>v.id===Number(d.editar)));
 if(d.excluir&&confirm("Excluir definitivamente este veículo?")){await api("/veiculos/"+d.excluir,"DELETE");await carregarVeiculos();avisar("Veículo excluído com sucesso.");}
 if(d.editarMarca){const m=marcas.find(x=>x.id===Number(d.editarMarca));$("#formMarca").elements.id.value=m.id;$("#formMarca").elements.nome.value=m.nome;}
 if(d.editarModelo){const m=modelos.find(x=>x.id===Number(d.editarModelo));const f=$("#formModelo");for(const k of ["id","nome","marcaId"])f.elements[k].value=m[k];}
 if(d.excluirMarca&&confirm("Excluir esta marca?")){await api("/marcas/"+d.excluirMarca,"DELETE");await carregarCatalogo();avisar("Marca excluída.");}
 if(d.excluirModelo&&confirm("Excluir este modelo?")){await api("/modelos/"+d.excluirModelo,"DELETE");await carregarCatalogo();avisar("Modelo excluído.");}
}catch(err){avisar(err.message,true)}});
executar(async()=>{await carregarCatalogo();await carregarVeiculos()})();
