package artGame.ui;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;


public class TestWindow {
	
	private static GLFWErrorCallback errorCallback = Callbacks.errorCallbackPrint(System.err);
	private static long window;
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	            glfwSetWindowShouldClose(window, GL_TRUE);
	        }
	    }
	};
	
	public TestWindow() {
		glfwSetErrorCallback(errorCallback);
		
		if (glfwInit() != GL_TRUE) {
		    throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		// create window
		window = glfwCreateWindow(640, 480, "Renderer Demo Window", NULL, NULL);
		if (window == NULL) {
		    glfwTerminate();
		    throw new RuntimeException("Failed to create the GLFW window");
		}
		
		// associate window with key callback
		glfwSetKeyCallback(window, keyCallback);
		
		// create OpenGL context
		glfwMakeContextCurrent(window);
		GLContext.createFromCurrent();
		
		// no proper 'game loop', as this is a test.
		// TODO associate proper Window class with Game class
		while (glfwWindowShouldClose(window) != GL_TRUE) {
			
			// swap buffers for double buffering
			glfwSwapBuffers(window);
			
			// poll key events
            glfwPollEvents();
		}
		
		// shut down
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	public static void main(String[] args) {
		new TestWindow();
	}

}
