from keras.datasets import mnist
from keras.utils import to_categorical
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten, Conv2D, MaxPooling2D
from keras.utils import plot_model
from keras.preprocessing.image import ImageDataGenerator

from keras.layers.normalization import BatchNormalization
import matplotlib.pyplot as plt

(x_train, y_train), (x_test, y_test) = mnist.load_data()

#image_index = 35
#print(y_train[image_index])
#plt.imshow(x_train[image_index], cmap='Greys')
#plt.show()

print(x_train.shape)
print(x_test.shape)

img_rows, img_cols = 28, 28
x_train = x_train.reshape(x_train.shape[0], img_rows, img_cols, 1)
x_test = x_test.reshape(x_test.shape[0], img_rows, img_cols, 1)

x_train = x_train / 255
x_test = x_test / 255

num_classes = 10

y_train = to_categorical(y_train, num_classes)
y_test = to_categorical(y_test, num_classes)

model = Sequential()
model.add(Conv2D(32, kernel_size=(3, 3), activation='relu', input_shape=(img_rows, img_cols, 1)))
model.add(Conv2D(64, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(rate=0.25))
model.add(Flatten())
model.add(Dense(128, activation='relu'))
model.add(Dropout(rate=0.5))
model.add(Dense(num_classes, activation='softmax'))
plot_model(model, to_file='model.png')

model.summary()

model.compile(loss='categorical_crossentropy',optimizer='adam', metrics=['accuracy'])

batch_size = 128
epochs = 10

aug_gen = ImageDataGenerator(rotation_range=15, zoom_range=0.2, width_shift_range = 4 , height_shift_range = 4)
aug_gen.fit(x_train)

print ("Training:")
history = model.fit_generator(aug_gen.flow(x_train, y_train, batch_size=batch_size), steps_per_epoch = x_train.shape[0] // batch_size, epochs=epochs, verbose=2, validation_data=(x_test, y_test))

print ("Evaluation:")
score = model.evaluate(x_test, y_test, verbose=2)
print('Test loss:', score[0])
print('Test accuracy:', score[1])
model.save("test_model_aug.h5")

# list all data in history
print(history.history.keys())
# summarize history for accuracy
plt.plot(history.history['accuracy'])
plt.plot(history.history['val_accuracy'])
plt.title('model accuracy')
plt.ylabel('accuracy')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.show()
# summarize history for loss
plt.plot(history.history['loss'])
plt.plot(history.history['val_loss'])
plt.title('model loss')
plt.ylabel('loss')
plt.xlabel('epoch')
plt.legend(['train', 'test'], loc='upper left')
plt.show()