package artGame.ui;

import artGame.ui.renderer.Asset;
import artGame.ui.renderer.AssetLoader;
import artGame.ui.renderer.Model;
import artGame.ui.renderer.math.Matrix4f;
import artGame.ui.renderer.math.Vector3f;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
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
	private static Matrix4f camera;
	private float angle = 35.2f;
	private float speed = 0.01f;
	private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
	    @Override
	    public void invoke(long window, int key, int scancode, int action, int mods) {
	        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
	            glfwSetWindowShouldClose(window, GL_TRUE);
	        }
	    }
	};
	
	public TestWindow() {
		camera = Matrix4f.translate(new Vector3f(0f, 0f, -1f));//.multiply(Matrix4f.rotate(angle, 1f, 0f, 0f));
		glfwSetErrorCallback(errorCallback);
		
		if (glfwInit() != GL_TRUE) {
		    throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		// create OpenGL 3.2 window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
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
		
		// declare buffers for using inside the loop
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        
        // temporary list of assets so something can be displayed
        // TODO replace with better scene-loading solution from game
        List<Asset> renderList = createScene();
		
		// no proper 'game loop', as this is a test.
		// TODO associate proper Window class with Game class
		while (glfwWindowShouldClose(window) != GL_TRUE) {
			
			float ratio;

            /* Get width and height to calcualte the ratio */
            glfwGetFramebufferSize(window, width, height);
            ratio = width.get() / (float) height.get();

            /* Rewind buffers for next get */
            width.rewind();
            height.rewind();

            /* Set viewport and clear screen */
            glViewport(0, 0, width.get(), height.get());
            glClear(GL_COLOR_BUFFER_BIT);
            
            for (Asset a : renderList) {
            	a.draw(camera);
            }

            /* Swap buffers and poll Events */
            glfwSwapBuffers(window);
            glfwPollEvents();

            /* Flip buffers for next loop */
            width.flip();
            height.flip();
            
            //camera = camera.multiply(Matrix4f.rotate(speed, 0f, 1f, 0f));
		}
		
		// shut down
		glfwDestroyWindow(window);
		keyCallback.release();
		glfwTerminate();
		errorCallback.release();
	}

	private List<Asset> createScene() {
		List<Asset> scene = new ArrayList<Asset>();
		Model stair = AssetLoader.instance().loadOBJ("res/stair.obj");
		if (stair != null) {
			scene.add(stair);
		}
		return scene;
	}

	public static void main(String[] args) {
		new TestWindow();
	}

}
