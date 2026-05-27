//DEFINICION DE VARIABLES
const titulo = document.querySelector("#titulo");
const parrafo = document.querySelector("#parrafo");
const inputNombreP = document.querySelector("#input-nombreP");
const inputPrecio = document.querySelector("#input-precio");
const inputDescripcion = document.querySelector("#input-descripcion");
const listaDatos = document.querySelector("#lista-datos");
const btnAgregar = document.querySelector("#btn-agregar");
const formulario = document.querySelector("#formulario");
const url = "http://localhost:3000/productos";
let datos = [];
document.addEventListener("DOMContentLoaded",() => {
    obtenerProductosDB();
});

//EVENTO ESCUCHA SUBMIT DEL FORM
formulario.addEventListener("submit", (event)=> {
    event.preventDefault(); // Evita que el formulario se envíe de forma predeterminada
    const nombreP = inputNombreP.value.trim();
    const precio = inputPrecio.value.trim();
    const descripcion = inputDescripcion.value.trim();
    if(nombreP==="" || precio === "" || descripcion === "") {
        console.error("Los campos no pueden estar vacíos.");
        return;
    }
    if(isNaN(Number(precio))|| Number(precio) <= 0) {
        console.error("El precio debe ser un número válido y positivo.");
        return;
    }
    const precioNumero = Number(precio);
    datos.push({nombreP,precioNumero,descripcion});
    localStorage.setItem("datos",JSON.stringify(datos));
    listarDatos();
    console.log("%c✔ Éxito: El producto se guardó correctamente.", "color: #2ecc71; font-weight: bold;");
    formulario.reset();
});
//FUNCION PARA ELIMINAR DATO
function eliminarDato(event){
    if(event.target.tagName==='BUTTON'){
        const datoABorrar = event.target.parentElement;
        const index = Array.from(listaDatos.children).indexOf(datoABorrar);
        datos.splice(index, 1);
        localStorage.setItem("datos",JSON.stringify(datos));
        listaDatos.removeChild(datoABorrar);
        console.log("Dato eliminado correctamente");
    }
}
//lLAMADA AL ADDLISTENER DEL CLICK PARA EL BOTON Del LI
listaDatos.addEventListener("click",eliminarDato);

//FUNCION PARA IMPRIMIR DATOS EN EL DOM 
function listarDatos(){
    listaDatos.innerHTML = "";
    datos.forEach(dato => {
        const nuevoLi =document.createElement("li");
        const btnX = document.createElement("button");
        btnX.classList="btn-eliminar";
        btnX.textContent="X";
        btnX.style.margin="20px";
        nuevoLi.textContent = `Producto: ${dato.nombreP}, Precio: ${dato.precioNumero}, Descripción: ${dato.descripcion}`;
        nuevoLi.appendChild(btnX);
        listaDatos.appendChild(nuevoLi);
    });
    
}
//LLAMADO A LISTAR DATOS CADA QUE RECARGA LA PAGINA
//CONSOLE PARA NOTAS CARGADAS
async function obtenerProductosDB(){
    try{
        const response = await fetch(url);
        if(!response.ok){
            throw new Error(`Error en el servidor: ${response.status}.`);
        }
        datos = await response.json();
        listarDatos();
        console.log(`Se han cargado correctamente ${datos.length} datos de la API.` )
    }catch(error){
        console.error("No se pudieron cagar los datos de la API:", error.message);
    }
    // response = await fetch();
    // return response;
}
async function crearProductoDB () {
    try{
        const response = await fetch(url,{
            method:"POST", headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(nuevoProducto)
    });
}
}