package model.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import model.dao.MoedaDAO;
import model.vo.MoedaVO;




public class MoedaBO {

	private byte[] converterByteParaArray (InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read = 0;
        byte[] dados = new byte[1024];
        while ((read = inputStream.read(dados, 0, dados.length)) != -1) {
            buffer.write(dados, 0, read);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public MoedaVO registerCoinBO(InputStream moedaInputStream, InputStream fileInputStream, FormDataContentDisposition fileMetaData){
        MoedaDAO moedaDAO = new MoedaDAO();
        MoedaVO moedaVO = null;
        try {
            byte[] arquivo = this.converterByteParaArray(fileInputStream);
            String moedaJSON = new String(this.converterByteParaArray(moedaInputStream), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();

            moedaVO = objectMapper.readValue(moedaJSON, MoedaVO.class);
            moedaVO.setImagem(arquivo);

            if (moedaDAO.verificarCadastroMoedaDAO(moedaVO)) {
                System.out.println("Essa moeda já existe no Banco de dados.");
            } else {
                moedaVO = moedaDAO.cadastrarMoedaDAO(moedaVO);
            }
        } catch (FileNotFoundException erro) {
            System.out.println("deu merda nessa porra BO !" + erro);
        } catch (IOException erro) {
            erro.printStackTrace();
        }
        return moedaVO;
    }
    //
    public boolean editCoinBO(InputStream moedaInputStream, InputStream fileInputStream, FormDataContentDisposition fileMetaData) {
        boolean resultado = false;
        MoedaDAO moedaDAO = new MoedaDAO();
        MoedaVO  moedaVO = null;
        try{
            byte[] arquivo = null;
            if (fileInputStream != null) {
                arquivo = this.converterByteParaArray(fileInputStream);
            }
            String moedaJSON = new String(this.converterByteParaArray(moedaInputStream), StandardCharsets.UTF_8);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            moedaVO = objectMapper.readValue(moedaJSON, MoedaVO.class);
            if (arquivo.length > 0) {
                moedaVO.setImagem(arquivo);
            }

            if (moedaDAO.verificarCadastroMoedaDAO(moedaVO)) {
                resultado = moedaDAO.editCoinDAO(moedaVO);
            } else {
                System.out.println("Essa moeda não existe no Banco de Dados!");
            } 
        } catch (FileNotFoundException erro) {
            System.out.println(erro);
        } catch (IOException erro) {
            erro.printStackTrace();
        }
        return resultado;
    }

    public boolean deleteCoinBO(MoedaVO moedaVO) {
        boolean resultado = false;
        MoedaDAO moedaDAO = new MoedaDAO();

        if (moedaDAO.verificarCadastroMoedaDAO(moedaVO)) {
            resultado = moedaDAO.excluirMoedaDAO(moedaVO);
            System.out.println("moeda deletada com sucesso!");
        } else {
            System.out.println("Falha na tentativa de deletar a moeda!");
        } 
        return resultado;
    }

    public Response listCoinBO(int idUsuario) {
        MoedaDAO moedaDAO = new MoedaDAO();
        ArrayList<MoedaVO> listaMoedasVO = moedaDAO.consultarMoedaDAO(idUsuario);
        if (listaMoedasVO.isEmpty()) {
            System.out.println("Não há nenhuma moeda na lista!");
            return Response.status(Response.Status.NO_CONTENT).entity("Nenhum usuário encontrado").build();
           
        }
        
        MultiPart multiPart = new FormDataMultiPart();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        try {
            for (MoedaVO moedaVO : listaMoedasVO) {
                byte[] imagem = moedaVO.getImagem();
                moedaVO.setImagem(null);

                String moedaJSON = objectMapper.writeValueAsString(moedaVO);
                multiPart.bodyPart(new StreamDataBodyPart("moedaVO", new ByteArrayInputStream(moedaJSON.getBytes()),
                moedaVO.getIdMoeda() + "-moeda.json"));

                if (imagem != null) {
                    multiPart.bodyPart(new StreamDataBodyPart("imagem", new ByteArrayInputStream(imagem),
                    moedaVO.getIdMoeda() + "-imagem.jpeg"));
                }
            }
            return Response.ok(multiPart).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao processar resposta multipart.").build();
        }
        
    }
}

