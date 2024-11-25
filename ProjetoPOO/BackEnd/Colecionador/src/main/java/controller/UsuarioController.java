package controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.bo.UsuarioBO;
import model.vo.UsuarioVO;

@Path("/user")
public class UsuarioController {

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
		public UsuarioVO registerUserControl(UsuarioVO UsuarioVO) {
			UsuarioBO usuarioBO = new UsuarioBO();
			return usuarioBO.registerUserBO(UsuarioVO);
	}
	@PUT
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
			public Boolean editUserControl(UsuarioVO UsuarioVO) {
			UsuarioBO usuarioBO = new UsuarioBO();
			return usuarioBO.editUserBO(UsuarioVO);
}

	  @DELETE
	    @Path("/delete")
	    @Consumes(MediaType.APPLICATION_JSON)
	    @Produces(MediaType.APPLICATION_JSON)
	    public Response deleteUserControl(UsuarioVO usuarioVO) {
	        // Validação do parâmetro
	        if (usuarioVO == null || usuarioVO.getIdUsuario() == 0) {
	            return Response.status(Response.Status.BAD_REQUEST)
	                           .entity("O parâmetro 'idusuario' é obrigatório.")
	                           .build();
	        }

	        UsuarioBO usuarioBO = new UsuarioBO();
	        boolean resultado = usuarioBO.deleteUserBO(usuarioVO);

	        if (resultado) {
	            return Response.ok("Usuário deletado com sucesso!").build();
	        } else {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("Usuário não encontrado no sistema.")
	                           .build();
	        }
	    }
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
		public UsuarioVO LoginUserControl(UsuarioVO UsuarioVO) { 
			UsuarioBO usuarioBO = new UsuarioBO();
			return usuarioBO.loginUserBO(UsuarioVO);
	}
}
