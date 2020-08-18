from keras.models import load_model
import imageio
import numpy as np
from matplotlib import pyplot as plt

from keras.datasets import mnist
#"https://i.imgur.com/a3Rql9C.png"
#"C:\Users\IvanovSD\Desktop\NumberTest\3sample.png"

model1 = load_model("test_model.h5")
model2 = load_model("test_model_aug.h5")

image_index = 30
(x_train, y_train), (x_test, y_test) = mnist.load_data()

img_rows, img_cols = 28, 28
im = imageio.imread(r"C:\Users\IvanovSD\Desktop\NumberTest\3sample.png")
#im = imageio.imread("https://i.imgur.com/a3Rql9C.png")
im = np.dot(im[...,:3], [0.2989, 0.587, 0.114])
#im = im.reshape(1, img_rows, img_cols, 1)
print (im.shape)

'''
print ("Xtrain before")
print (x_test.shape[0])
print (x_test.shape)
x_test = x_test.reshape(x_test.shape[0], img_rows, img_cols, 1)
print ("Xtrain after")
print (x_test.shape[0])
print (x_test.shape)
im = x_test[image_index]
'''
gray = im
plt.imshow(gray, cmap='Greys')
plt.show()

'''
gray = np.dot(im[...,:3], [0.2989, 0.587, 0.114])
plt.imshow(gray, cmap = plt.get_cmap('gray'))
plt.show()
'''
# reshape the image
gray = gray.reshape(-1, img_rows, img_cols, 1)
print ("gray shape")
print (gray.shape)

# normalize image
gray = gray / 255

print ("MODEL1:")
prediction = model1.predict(gray)
print(prediction)
print(prediction.argmax())

print ("MODEL2:")
prediction = model2.predict(gray)
print(prediction)
print(prediction.argmax())