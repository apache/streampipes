package de.fzi.cep.sepa.rest.annotation;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by riemer on 30.08.2016.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface GsonWithoutIds {}
