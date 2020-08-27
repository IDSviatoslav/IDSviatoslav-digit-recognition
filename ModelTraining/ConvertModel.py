from keras.models import load_model
import imageio
import numpy as np
import tensorflow as tf
from matplotlib import pyplot as plt

#print(tf.__version__)

#model = load_model("test_model.h5")

img_rows, img_cols = 28, 28
im = imageio.imread(r"C:\Users\тс\PycharmProjects\NumberRec\sample.png")
im = np.dot(im[...,:3], [0.2989, 0.587, 0.114])

gray = im
plt.imshow(gray, cmap='Greys')
plt.show()

im = im.reshape(-1, img_rows, img_cols, 1)
im = im/255
im = np.asarray(im)
im = im.astype('float32')

print ('im shape ', im.shape)

#converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter = tf.lite.TFLiteConverter.from_keras_model_file("test_model_aug.h5")

tflite_model = converter.convert()
tflite_model_name = "converted_model_aug.tflite"
open(tflite_model_name, "wb").write(tflite_model)

# Load TFLite model and allocate tensors.
interpreter = tf.lite.Interpreter(model_path="converted_model_aug.tflite")
interpreter.allocate_tensors()
# Get input and output tensors
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

# Test model on random input data
#input_shape = input_details[0]['shape']
input_data = im
interpreter.set_tensor(input_details[0]['index'], input_data)
interpreter.invoke()
output_data = interpreter.get_tensor(output_details[0]['index'])
print(output_data)
print(output_data.argmax())