package de.fzi.cep.sepa.rest.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.imgscalr.Scalr;

import de.fzi.cep.sepa.commons.Utils;

@Path("/icons")
public class Icon {


	@Path("{iconId}")
	@GET
	@Produces("image/png")
	public byte[] getImage(@PathParam("iconId") String iconId, @DefaultValue("true") @QueryParam("hq") boolean hq, @QueryParam("width") int width, @QueryParam("height") int height)
	{
			String appendix = hq ? "_HQ.png" : ".png"; 
			String filename = Utils.getImageUrl() +iconId + appendix;
		
		 	BufferedImage image;
		 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 	
			try {
				image = ImageIO.read(new URL(filename));
			
				if (width != 0 && height != 0)
					image = Scalr.resize(image, width, height);
				ImageIO.write(image, "png", baos);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    byte[] imageData = baos.toByteArray();
		    return imageData;
	}
}
