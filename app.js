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
let idEdicion = null;

document.addEventListener("DOMContentLoaded",() => {
    datos = JSON.parse(localStorage.getItem("datos")) || [];
    listarDatos();
    obtenerProductosDB();
});

//EVENTO ESCUCHA SUBMIT DEL FORM
 formulario.addEventListener("submit",async (event)=> {
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
    const nuevoProducto={nombreP,precioNumero,descripcion};
    if(idEdicion!==null){
        await actualizarProductoDB(idEdicion, nuevoProducto);
    }
    else{
        await crearProductoDB(nuevoProducto);
    }
    console.log("%c✔ Éxito: El producto se guardó correctamente.", "color: #2ecc71; font-weight: bold;");
    formulario.reset();
});
//FUNCION PARA ELIMINAR DATO
async function eliminarDato(event){
    if(event.target.classList.contains('btn-eliminar')){
        const idABorrar = event.target.dataset.id;
        await eliminarProductoDB(idABorrar);
        console.log("Dato eliminado correctamente");
    }
    if(event.target.classList.contains('btn-editar')){
        const idEditar = event.target.dataset.id;
        const productoSeleccionado = datos.find(p=>p.id===idEditar);
        if(productoSeleccionado){
            idEdicion =idEditar;
            inputNombreP.value = productoSeleccionado.nombreP;
            inputPrecio.value = productoSeleccionado.precioNumero;
            inputDescripcion.value = productoSeleccionado.descripcion;
            
            // Opcional: Cambiamos el texto del botón principal para guiar al usuario
            btnAgregar.textContent = "Guardar Cambios";
            console.log("Cargando datos en el formulario para editar...");
        }
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
        btnX.type ="button";
        btnX.className="btn-eliminar";
        btnX.textContent="X";
        btnX.style.margin="20px";
        btnX.dataset.id=dato.id;
        const btnEditar = document.createElement("button");
        btnEditar.type= "button";
        btnEditar.className = "btn-editar";
        btnEditar.textContent = "Editar";
        btnEditar.style.margin = "10px";
        btnEditar.dataset.id = dato.id;
        nuevoLi.textContent = `Producto: ${dato.nombreP}, Precio: ${dato.precioNumero}, Descripción: ${dato.descripcion}`;
        nuevoLi.appendChild(btnX);
        nuevoLi.appendChild(btnEditar);
        listaDatos.appendChild(nuevoLi);
    });
    
}
//LLAMADO A LISTAR DATOS CADA QUE RECARGA LA PAGINA
//GET
async function obtenerProductosDB(){
    try{
        const response = await fetch(url);
        if(!response.ok){
            throw new Error(`Error en el servidor: ${response.status}.`);
        }
        datos = await response.json();
        localStorage.setItem("datos", JSON.stringify(datos));
        listarDatos();
        console.log(`Se han cargado correctamente ${datos.length} datos de la API.` )
    }catch(error){
        parrafo.textContent = "⚠️ Lo sentimos, no pudimos conectar con el servidor. Inténtalo más tarde.";
        parrafo.style.color = "red";

        }
}
//FUNCION POST
async function crearProductoDB (nuevoProducto) {
    try{
        const response = await fetch(url,{
            method:"POST", headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(nuevoProducto)
    });
    if(!response.ok){
        throw new Error(`no se pudo guardar en el servidor: ${response.status}`);
    }
    const productoCreadoConId =await response.json();
    datos.push(productoCreadoConId);
    localStorage.setItem("datos",JSON.stringify(datos));
    console.log("Producto creado correctamente en la API.");
    await obtenerProductosDB();
}
catch(error){
    parrafo.textContent = "⚠️ Lo sentimos, no pudimos conectar con el servidor. Inténtalo más tarde.";
    parrafo.style.color = "red"; // Para que llame la atención
}
}
//DELETE
async function eliminarProductoDB(id) {
    try{
        const response = await fetch (`${url}/${id}`,{
            method: "DELETE"
        });
        if(!response.ok){
            throw new Error(`No se pudo eliminar el producto: ${response.status}`);
        }
        datos =datos.filter(p=>p.id!==id);
        localStorage.setItem("datos",JSON.stringify(datos));
        console.log("Producti eliminado correctamente de la API.")
        await obtenerProductosDB();
    }
    catch(error){
        console.error("Error al eliminar el producto en la API: ",error.message);
    }
}
//PUT
async function actualizarProductoDB(id, productoModificado) {
    try {
        // Apuntamos al elemento exacto agregando el ID a la URL (ej: /productos/1)
        const response = await fetch(`${url}/${id}`, {
            method: "PUT", // Indicamos al servidor que reemplace los datos antiguos
            headers: {
                "Content-Type": "application/json" // Avisamos que enviamos JSON
            },
            body: JSON.stringify(productoModificado) // Convertimos el objeto a texto
        });

        if (!response.ok) {
            throw new Error(`No se pudo actualizar en el servidor: ${response.status}`);
        }
        const index = datos.findIndex(p => p.id === id);
        if (index !== -1) {
            datos[index] = { id, ...productoModificado };
            localStorage.setItem("datos", JSON.stringify(datos));
        }
        console.log("Producto actualizado correctamente en la API.");
        // 🔄 LIMPIEZA: Devolvemos el formulario a su estado normal de creación
        idEdicion = null; // Olvidamos el ID porque ya terminamos de editar
        btnAgregar.textContent = "Agregar"; // El botón vuelve a su texto original
        
        // Refrescamos la pantalla con la lista actualizada del servidor
        await obtenerProductosDB();

    } catch (error) {
        parrafo.textContent = "⚠️ Lo sentimos, no pudimos actualizar el producto.";
        parrafo.style.color = "red";
        console.error("Error en el PUT:", error.message);
    }
}
