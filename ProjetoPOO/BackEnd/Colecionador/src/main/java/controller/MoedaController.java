package controller;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import model.bo.MoedaBO;
import model.vo.MoedaVO;
@Path("/coin")
public class MoedaController {

	@POST
	@Path("/register")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public MoedaVO registerCoinController(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileMetaData,
			@FormDataParam("moedaVO") InputStream moedaInputStream) throws Exception {
			MoedaBO moedaBO = new MoedaBO();
			return moedaBO.registerCoinBO(moedaInputStream, fileInputStream, fileMetaData);
		
	}
	
	@POST
	@Path("/edit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean editCoinController(@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam ("file") FormDataContentDisposition fileMetaData,
			@FormDataParam("moedaVO") InputStream moedaInputStream) throws Exception {
				MoedaBO moedaBO = new MoedaBO();
				return moedaBO.editCoinBO(moedaInputStream, fileInputStream, fileMetaData);
			}
	@GET
	@Path("/list/{idusuario}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.MULTIPART_FORM_DATA)
	public Response listCoinController(@PathParam("idUsuario") int idUsuario) {
		MoedaBO moedaBO = new MoedaBO();
		return moedaBO.listCoinBO(idUsuario);
	}
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean deleteUserControl(MoedaVO moedaVO) {
		MoedaBO MoedaBO = new MoedaBO();
		return MoedaBO.deleteCoinBO(moedaVO);
	}
}

