// Seleção de elementos do DOM
const form = document.querySelector('#formulario');
const limpar = document.querySelector('#limpar');
const exibir = document.querySelector('#exibir');
const principal = document.querySelector('.principal');
const inputFile = document.getElementById('arquivo');
const fileName = document.getElementById('nome-arquivo');
const usuarioLogadoStr = sessionStorage.getItem("usuarioLogado");
const usuarioLogado = JSON.parse(usuarioLogadoStr);
const userId = usuarioLogado?.idUsuario || null; // Garante que o ID do usuário esteja presente
let moeda = {}; // Inicialização correta no escopo global

// Atualiza o nome do arquivo quando selecionado
inputFile.addEventListener('change', function () {
    if (inputFile.files.length > 0) {
        fileName.textContent = inputFile.files[0].name;
    } else {
        fileName.textContent = 'Nenhum arquivo selecionado';
    }
});

// Controle de exibição da aba principal
exibir.addEventListener("click", function () {
    if (principal.style.display === 'flex') {
        principal.style.display = 'none'; // Oculta a aba
        exibir.textContent = 'Mostrar';
    } else {
        principal.style.display = 'flex'; // Mostra a aba
        exibir.textContent = 'Ocultar';
    }
});

// Função para formatar datas no padrão brasileiro (DD/MM/AAAA)
function formatarDataPadraoBrasil(data) {
    let dataFormatada = new Date(data),
        dia = dataFormatada.getDate().toString().padStart(2, '0'),
        mes = (dataFormatada.getMonth() + 1).toString().padStart(2, '0'),
        ano = dataFormatada.getFullYear();
    return dia + "/" + mes + "/" + ano;
}

// Função para formatar datas no padrão americano (AAAA-MM-DD)
function formatarDataPadraoAmericano(data) {
    let dataFormatada = new Date(data),
        dia = dataFormatada.getDate().toString().padStart(2, '0'),
        mes = (dataFormatada.getMonth() + 1).toString().padStart(2, '0'),
        ano = dataFormatada.getFullYear();
    return ano + "-" + mes + "-" + dia;
}

// Reseta o formulário e oculta o nome do arquivo
limpar.addEventListener('click', () => {
    form.reset();
    arquivo.value = '';
    fileName.textContent = 'Nenhum arquivo selecionado';
});

// Função para cadastrar uma nova moeda
async function cadastrarMoeda(formData) {
    console.log("Iniciando cadastro de moeda...");
    console.log([...formData.entries()]); // Mostra todos os campos do FormData
    let options = {
        method: "POST",
        body: formData
    };
    try {
        const moedaJson = await fetch('http://localhost:8080/colecionador/rest/coin/register', options);
        const moedaVO = await moedaJson.json();
        console.log("Resposta do servidor:", moedaVO);
        if (moedaVO.idMoeda != 0) {
            alert("Cadastro realizado com sucesso.");
            moeda = {};
            form.reset();
            principal.style.display = 'none';
            exibir.textContent = 'Mostrar';
            fileName.textContent = 'Nenhum arquivo selecionado.';
            buscarMoedas();
        } else {
            alert("Houve um problema no cadastro da moeda.");
        }
    } catch (error) {
        console.error("Erro ao cadastrar moeda:", error);
    }
}

// Função para atualizar uma moeda existente
async function atualizarMoeda(formData) {
    try {
        const options = {
            method: 'PUT',
            body: formData,
        };

        const response = await fetch('http://localhost:8080/colecionador/rest/coin/edit', options);

        if (response.ok) {
            alert('Moeda atualizada com sucesso.');
            preencherTabela(await buscarMoedas()); // Atualiza a tabela
            principal.style.display = 'none'; // Oculta o formulário
            exibir.textContent = 'Mostrar';
        } else {
            alert('Erro ao atualizar a moeda.');
        }
    } catch (error) {
        console.error('Erro ao atualizar a moeda:', error);
    }
}

// Função para excluir uma moeda
async function excluirMoeda(dados) {
    let options = {
        method: "DELETE",
        headers: { "Content-type": "application/json" },
        body: JSON.stringify({
            idMoeda: dados.idMoeda,
            nome: dados.nome,
            pais: dados.pais,
            ano: dados.ano,
            valor: dados.valor,
            detalhes: dados.detalhes
        })
    };

    const resultado = await fetch('http://localhost:8080/colecionador/rest/coin/delete', options);
    if (resultado.ok == true) {
        alert("Exclusão realizada com sucesso.");
        moeda = {};
        window.location.reload();
    } else {
        alert("Houve um problema na exclusão da moeda.");
    }
}

// Função para buscar moedas associadas ao usuário
async function buscarMoedas() {
    let options = {
        method: "GET"
    };
    const response = await fetch(`http://localhost:8080/colecionador/rest/coin/list/${userId}`, options);

    if (response.status === 204) {
        alert("Nenhuma moeda encontrada.");
        return;
    }

    const moedas = await response.json(); // Supondo que o backend retorna uma lista JSON

    let tbody = document.querySelector('#tbody');
    tbody.innerHTML = ''; // Limpa a tabela antes de popular

    moedas.forEach((moeda) => {
        const tableRow = document.createElement('tr');

        // Coluna da imagem
        const imageCell = document.createElement('td');
        const imgElement = document.createElement('img');
        imgElement.src = moeda.imagemUrl; // Supondo que há um campo "imagemUrl" retornado pelo backend
        imgElement.width = 100; // Ajuste conforme necessário
        imgElement.height = 100;
        imageCell.appendChild(imgElement);
        tableRow.appendChild(imageCell);

        // Outras colunas
        ['id', 'nome', 'pais', 'ano', 'valor', 'detalhes'].forEach((key) => {
            const cell = document.createElement('td');
            cell.textContent = moeda[key];
            tableRow.appendChild(cell);
        });

        // Coluna de ações
        const actionsCell = document.createElement('td');

        // Botão "Editar"
        const editar = document.createElement('button');
        editar.textContent = 'Editar';
        editar.addEventListener('click', () => {
            // Preenche o formulário para edição
            document.getElementById('nome').value = moeda.nome;
            document.getElementById('pais').value = moeda.pais;
            document.getElementById('ano').value = moeda.ano;
            document.getElementById('valor').value = moeda.valor;
            document.getElementById('detalhes').value = moeda.detalhes;

            form.dataset.action = 'atualizar'; // Define a ação como "atualizar"
            form.dataset.moedaId = moeda.id; // Salva o ID da moeda no formulário

            principal.style.display = 'flex'; // Mostra o formulário
            exibir.textContent = 'Ocultar';
        });
        actionsCell.appendChild(editar);

        // Botão "Excluir"
        const excluir = document.createElement('button');
        excluir.textContent = 'Excluir';
        excluir.addEventListener('click', () => {
            if (confirm(`Tem certeza que deseja excluir "${moeda.nome}"?`)) {
                excluirMoeda(moeda);
            }
        });
        actionsCell.appendChild(excluir);

        tableRow.appendChild(actionsCell);
        tbody.appendChild(tableRow);
    });
}

// Chamada inicial para buscar moedas
buscarMoedas();

// Manipula o envio do formulário
form.addEventListener('submit', async (evento) => {
    evento.preventDefault(); // Impede o envio padrão do formulário

    const action = form.dataset.action; // Define se a ação é "cadastrar" ou "atualizar"
    const idMoeda = form.dataset.moedaId || null; // ID para atualização, se disponível
    const file = inputFile.files[0];

    if (file && file.size > 1 * 1024 * 1024) {
        alert('O arquivo é muito grande. O tamanho máximo permitido é 1 MB.');
        return;
    }

    const moedaVO = {
        idMoeda,
        nome: document.getElementById('nome').value,
        pais: document.getElementById('pais').value,
        ano: document.getElementById('ano').value,
        valor: document.getElementById('valor').value,
        detalhes: document.getElementById('detalhes').value,
        idUsuario
    };

    const formData = new FormData();
    formData.append('moedaVO', new Blob([JSON.stringify(moedaVO)], { type: 'application/json' }));
    if (file) formData.append('file', file);

    if (action === 'atualizar') {
        await atualizarMoeda(formData);
    } else {
        await cadastrarMoeda(formData);
    }
});
