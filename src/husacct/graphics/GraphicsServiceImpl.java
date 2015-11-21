package husacct.graphics;

import husacct.common.savechain.ISaveable;
import husacct.common.services.ObservableService;
import husacct.graphics.presentation.GraphicsPresentationController;
import husacct.graphics.task.DrawingController;
import husacct.graphics.util.DrawingLayoutStrategy;

import javax.swing.JInternalFrame;

import org.apache.log4j.Logger;
import org.jdom2.Element;

public class GraphicsServiceImpl extends ObservableService implements IGraphicsService, ISaveable {
	
	private GraphicsPresentationController presentationControllersAnalysed;
	private GraphicsPresentationController presentationControllersDefined;
	protected Logger			logger							= Logger.getLogger(GraphicsServiceImpl.class);
	
	public static final String	workspaceServiceName			= "ArchitecureGraphicsService";
	
	public static final String	workspaceAnalysedControllerName	= "analysedController";
	
	public static final String	workspaceDefinedControllerName	= "definedController";
	
	public static final String	workspaceShowDependencies		= "showDependencies";
	
	public static final String	workspaceShowViolations			= "showViolations";
	
	public static final String	workspaceSmartLines				= "smartLines";
	
	public static final String	workspaceLayoutStrategy			= "layoutStrategy";
	
	public GraphicsServiceImpl() {
		
	}
	
	@Override
	public void drawAnalysedArchitecture() {
		createPresentationControllerAnalysed();
		presentationControllersAnalysed.drawArchitectureTopLevel();
	}
	
	@Override
	public void drawDefinedArchitecture() {
		createPresentationControllerDefined();
		presentationControllersDefined.drawArchitectureTopLevel();
	}
	
	@Override
	public JInternalFrame getAnalysedArchitectureGUI() {
		createPresentationControllerAnalysed();
		return presentationControllersAnalysed.getGraphicsFrame();
	}
	
	@Override
	public JInternalFrame getDefinedArchitectureGUI() {
		createPresentationControllerDefined();
		return presentationControllersDefined.getGraphicsFrame();
	}
	
	private void createPresentationControllerAnalysed() {
		if (presentationControllersAnalysed == null) {
			presentationControllersAnalysed = new GraphicsPresentationController("AnalysedDrawing");
		}
	}
	
	private void createPresentationControllerDefined() {
		if (presentationControllersDefined == null) {
			presentationControllersDefined = new GraphicsPresentationController("DefinedDrawing");
		}
	}
	
	private void createControllers() {
		createPresentationControllerAnalysed();
		createPresentationControllerDefined();
	}
	
	@Override
	public Element getWorkspaceData() {
		createControllers();
		Element data = new Element(workspaceServiceName);
		data.addContent(getWorkspaceDataForController(workspaceAnalysedControllerName, presentationControllersAnalysed.getController()));
		data.addContent(getWorkspaceDataForController(workspaceDefinedControllerName, presentationControllersDefined.getController()));
		return data;
	}
	
	private Element getWorkspaceDataForController(String controllerName, DrawingController controller) {
		Element controllerElement = new Element(controllerName);
		controllerElement.setAttribute(workspaceShowDependencies, "" + controller.areDependenciesShown());
		controllerElement.setAttribute(workspaceShowViolations, "" + controller.areViolationsShown());
		controllerElement.setAttribute(workspaceSmartLines, "" + controller.areSmartLinesOn());
		controllerElement.setAttribute(workspaceSmartLines, "" + controller.areSmartLinesOn());
		controllerElement.setAttribute(workspaceLayoutStrategy, controller.getLayoutStrategy().toString());
		return controllerElement;
	}
	
	private boolean isActive(Element controllerElement, String attribute) {
		return Boolean.parseBoolean(controllerElement.getAttribute(attribute).getValue());
	}
	
	@Override
	public void loadWorkspaceData(Element workspaceData) {
		createControllers();
		try {
			Element analysedControllerElement = workspaceData.getChild(workspaceAnalysedControllerName);
			loadWorkspaceDataForController(presentationControllersAnalysed.getController(), analysedControllerElement);
		} catch (Exception e) {
			logger.error("Error importing the workspace for analyse.", e);
		}
		try {
			Element definedControllerElement = workspaceData.getChild(workspaceDefinedControllerName);
			loadWorkspaceDataForController(presentationControllersDefined.getController(), definedControllerElement);
		} catch (Exception e) {
			logger.error("Error importing the workspace for define.", e);
		}
	}
	
	private void loadWorkspaceDataForController(DrawingController controller, Element data) {
		if (isActive(data, workspaceShowDependencies)) 
			controller.dependenciesShow();
		else
			controller.dependenciesHide();
		
		if (isActive(data, workspaceShowViolations)) 
			controller.violationsShow();
		else
			controller.violationsHide();
		
		if (isActive(data, workspaceSmartLines)) 
			controller.smartLinesEnable();
		else
			controller.smartLinesDisable();
		
		DrawingLayoutStrategy selectedStrategy = null;
		for (DrawingLayoutStrategy strategy : DrawingLayoutStrategy.values())
			if (strategy.toString().equals( data.getAttribute(workspaceLayoutStrategy).getValue())) 
				selectedStrategy = strategy;
		if (null != selectedStrategy) 
			controller.layoutStrategyChange(selectedStrategy);
	}
}