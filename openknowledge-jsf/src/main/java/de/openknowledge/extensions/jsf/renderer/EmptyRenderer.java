package de.openknowledge.extensions.jsf.renderer;

import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = "javax.faces.Input", rendererType = "de.openknowledge.EmtpyRenderer")
public class EmptyRenderer extends Renderer {

}
