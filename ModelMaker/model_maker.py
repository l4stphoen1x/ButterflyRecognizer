#
# Copyright 2021 Gerry(gpiosenka). All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Modifications copyright (C) 2021 Andrii Hubert

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.optimizers import Adamax
from tensorflow.keras import regularizers
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.models import Model
import numpy as np
import pandas as pd
import shutil
import time
import matplotlib.pyplot as plt
import os
import seaborn as sns
sns.set_style('darkgrid')
from sklearn.metrics import confusion_matrix, classification_report
def text_design(message, foreground, background):
    rb, gb, bb = background
    rf, gf, bf = foreground
    message = '{0}' + message
    mat = '\33[38;2;' + str(rf) + ';' + str(gf) + ';' + str(bf) + ';48;2;' + str(rb) + ';' + str(gb) + ';' + str(bb) + 'm'
    print(message.format(mat), flush=True)
    print('\33[0m', flush=True)
    return
f_path = r'./test/Aglais io/1.jpg'
home = r'./'
for categorization in ['train', 'test', 'validation']:
    fpaths_list = []
    tags_list = []
    c_path = os.path.join(home, categorization)
    c_list = os.listdir(c_path)
    for c in c_list:
        c_path_helper = os.path.join(c_path, c)
        f_list = os.listdir(c_path_helper)
        for file in f_list:
            f_path = os.path.join(c_path_helper, file)
            fpaths_list.append(f_path)
            tags_list.append(c)
    paths_series = pd.Series(fpaths_list, name='fpaths_list')
    tags_series = pd.Series(tags_list, name='tags_list')
    if categorization == 'train':
        amount_train = pd.concat([paths_series, tags_series], axis=1)
    elif categorization == 'test':
        amount_test = pd.concat([paths_series, tags_series], axis=1)
    else:
        amount_validation = pd.concat([paths_series, tags_series], axis=1)
print('Liczba obrazów do treningu:', len(amount_train), '| Liczba obrazów testowych:', len(amount_test), '| Liczba obrazów do walidacji:', len(amount_validation))
amount_class = list(amount_train['tags_list'].value_counts())
print('Liczba obrazów w każdej klasie:', amount_class)
print('Liczba obrazów w każdej klasie, w których liczba obrazów są mniejsze niż docelowa liczba augmentacji:')
amount_class_helper1 = amount_train.groupby('tags_list')
augment_min = 0
augment_target = 150
h_list = []
for tag in amount_train['tags_list'].unique():
    amount_class_helper2 = amount_class_helper1.get_group(tag)
    count_helper = len(amount_class_helper2)
    if count_helper > augment_target:
        img_examples = amount_class_helper2.sample(augment_target, replace=False, weights=None, random_state=123, axis=0).reset_index(drop=True)
        h_list.append(img_examples)
    elif count_helper >= augment_min:
        h_list.append(amount_class_helper2)
augment_dir = os.path.join(home, 'augment')
amount_train = pd.concat(h_list, axis=0).reset_index(drop=True)
if os.path.isdir(augment_dir):
    shutil.rmtree(augment_dir)
os.mkdir(augment_dir)
for tag in amount_train['tags_list'].unique():
    aug_savepath = os.path.join(augment_dir, tag)
    os.mkdir(aug_savepath)
img_gen = ImageDataGenerator(horizontal_flip=True, rotation_range=25, width_shift_range=.11, height_shift_range=.15, zoom_range=.15)
amount_class_helper1 = amount_train.groupby('tags_list')
augment_target_helper = augment_target
for tag in amount_train['tags_list'].unique():
    amount_class_helper2 = amount_class_helper1.get_group(tag)
    count_helper = len(amount_class_helper2)
    if count_helper < augment_target_helper:
        target_dir = os.path.join(augment_dir, tag)
        aug_img_gen = img_gen.flow_from_dataframe(amount_class_helper2, x_col='fpaths_list', y_col=None, target_size=(224, 224), class_mode=None, batch_int=1, shuffle=False, save_to_dir=target_dir, save_prefix='aug_', save_format='jpg')
        aug_counting = augment_target_helper - count_helper
        aug_amount = 0
        while aug_amount < aug_counting:
            aug_examples = next(aug_img_gen)
            aug_amount += len(aug_examples)
c_list = os.listdir(augment_dir)
augment_tags = []
augment_fpaths = []
for c in c_list:
    c_path_helper = os.path.join(augment_dir, c)
    f_list = os.listdir(c_path_helper)
    for file in f_list:
        f_path = os.path.join(c_path_helper, file)
        augment_tags.append(c)
        augment_fpaths.append(f_path)
tags_series = pd.Series(augment_tags, name='tags_list')
paths_series = pd.Series(augment_fpaths, name='fpaths_list')
amount_aug = pd.concat([paths_series, tags_series], axis=1)
amount_aug_plus_train = pd.concat([amount_train, amount_aug], axis=0).reset_index(drop=True)
print('Sprawdzenie poprawności augmentacji:')
print(list(amount_aug_plus_train['tags_list'].value_counts()))
def imgscaler(image_phelper):
    image_phelper = image_phelper / 127.5 - 1
    return image_phelper
img_width = 224
img_height = 224
img_resolution = (img_height, img_width)
batch_int = 40
img_channels = 3
img_shape = (img_height, img_width, img_channels)
amount_test_length = len(amount_test)
batch_test = sorted([int(amount_test_length / n) for n in range(1, amount_test_length + 1) if amount_test_length % n == 0 and amount_test_length / n <= 80], reverse=True)[0]
test_stages = int(amount_test_length / batch_test)
print('Wielkość partii testowej: ', batch_test, '  Kroków testowych: ', test_stages)
print('Aktualna liczba zdjęć we wszystkich zestawach:')
testvalid_generator_helper = ImageDataGenerator(preprocessing_function=imgscaler)
validation_generator = testvalid_generator_helper.flow_from_dataframe(amount_validation, x_col='fpaths_list', y_col='tags_list', target_size=img_resolution, class_mode='categorical', color_mode='rgb', shuffle=True, batch_int=batch_int)
test_generator = testvalid_generator_helper.flow_from_dataframe(amount_test, x_col='fpaths_list', y_col='tags_list', target_size=img_resolution, class_mode='categorical', color_mode='rgb', shuffle=False, batch_int=batch_test)
train_generator_helper = ImageDataGenerator(preprocessing_function=imgscaler, horizontal_flip=True)
train_generator = train_generator_helper.flow_from_dataframe(amount_train, x_col='fpaths_list', y_col='tags_list', target_size=img_resolution, class_mode='categorical',color_mode='rgb', shuffle=True, batch_int=batch_int)
categories = list(train_generator.class_indices.keys())
train_stages = np.ceil((len(train_generator.labels) / batch_int))
c_counter = len(categories)
m_base = tf.keras.applications.InceptionResNetV2(include_top=False, weights="imagenet", input_shape = img_shape, pooling='max')
x = m_base.output
x = keras.layers.BatchNormalization(axis = -1, momentum = 0.99, epsilon = 0.001)(x)
x = Dense(256, kernel_regularizer=regularizers.l2(l=0.016), activity_regularizer=regularizers.l1(0.006), bias_regularizer=regularizers.l1(0.006), activation='relu')(x)
x = Dropout(rate=.45, seed = 123)(x)
output = Dense(c_counter, activation='softmax')(x)
model = Model(inputs = m_base.input, outputs = output)
model.compile(Adamax(learning_rate=.001), loss='categorical_crossentropy', metrics=['accuracy'])
class LRA(keras.callbacks.Callback):
    def __init__(self, model, m_base, sufferance, sufferance_stop, threshold_accuracy, delta_lr_reduce, bestepoch_boolean, batch_per_epoch, initial_epoch, epochs, epoch_rethinking):
        super(LRA, self).__init__()
        self.model = model
        self.m_base = m_base
        self.sufferance = sufferance
        self.sufferance_stop = sufferance_stop
        self.threshold_accuracy = threshold_accuracy
        self.delta_lr_reduce = delta_lr_reduce
        self.bestepoch_boolean = bestepoch_boolean
        self.batch_per_epoch = batch_per_epoch
        self.initial_epoch = initial_epoch
        self.epochs = epochs
        self.epoch_rethinking = epoch_rethinking
        self.epoch_rethinking_initial = epoch_rethinking
        self.counting = 0
        self.c_stop = 0
        self.lowestloss_epoch = 1
        self.savelr = float(tf.keras.backend.get_value(model.optimizer.lr))
        self.max_trainacc = 0.0
        self.min_validloss = np.inf
        self.setbetter_weights = self.model.get_weights()
        self.weights_save = self.model.get_weights()
    def on_train_begin(self, logs=None):
        if self.m_base != None:
            position = m_base.trainable = False
            if position:
                message = 'Inicjowanie wywołania zwrotnego uruchamiającego szkolenie z m_base.trainable'
            else:
                message = 'Inicjowanie wywołania zwrotnego uruchamiającego szkolenie z m_base not trainable'
        else:
            message = 'Inicjowanie oddzwaniania i rozpoczęcie szkolenia'
        text_design(message, (244, 252, 3), (55, 65, 80))
        message = '{0:^10s}{1:^7s}{2:^7s}{3:^9s}{4:^1s}{5:^7s}{6:^7s}{7:^7s}{8:^7s}'.format('Epoka', 'Strata', 'Dokładność ', 'V_Strata', 'V_Dokładność', 'LR', 'Następny_LR ', 'Condition ',  'Trwanie')
        text_design(message, (244, 252, 3), (55, 65, 80))
        self.t_start = time.time()
    def on_train_end(self, logs=None):
        t_stop = time.time()
        train_endurance = t_stop - self.t_start
        t_hh = train_endurance // 3600
        t_mm = (train_endurance - (t_hh * 3600)) // 60
        self.model.set_weights(self.setbetter_weights)
        message = f'Trening zakończony - model jest ustawiony z ciężarami z epoki {self.lowestloss_epoch} '
        text_design(message, (0, 255, 0), (55, 65, 80))
        t_ss = train_endurance - ((t_hh * 3600) + (t_mm * 60))
        message = f'Czas treningu wynosił {str(t_hh)} godziny, {t_mm:4.1f} minut, {t_ss:4.2f} sekund)'
        text_design(message, (0, 255, 0), (55, 65, 80))
    def on_train_batch_end(self, batch, logs=None):
        train_accuracy = logs.get('accuracy') * 100
        loss = logs.get('loss')
        message = '{0:20s}przetwarzanie partii {1:4s} z {2:5s} dokładnością= {3:8.3f}  strata: {4:8.5f}'.format(' ', str(batch), str(self.batch_per_epoch), train_accuracy, loss)
        print(message, '\r', end='')
    def on_epoch_begin(self, epoch, logs=None):
        self.now = time.time()
    def on_epoch_end(self, epoch, logs=None):
        later = time.time()
        endurance = later - self.now
        learning_rate = float(tf.keras.backend.get_value(self.model.optimizer.lr))
        actual_lr = learning_rate
        loss = logs.get('loss')
        validation_accuracy = logs.get('val_accuracy')
        train_accuracy = logs.get('accuracy')
        validation_loss = logs.get('val_loss')
        if train_accuracy < self.threshold_accuracy:
            condition = 'accuracy'
            if train_accuracy > self.max_trainacc:
                self.max_trainacc = train_accuracy
                self.setbetter_weights = self.model.get_weights()
                self.counting = 0
                self.c_stop = 0
                if validation_loss < self.min_validloss:
                    self.min_validloss = validation_loss
                color = (0, 255, 0)
                self.lowestloss_epoch = epoch + 1
            else:
                if self.counting >= self.sufferance - 1:
                    color = (245, 170, 66)
                    learning_rate = learning_rate * self.delta_lr_reduce
                    tf.keras.backend.set_value(self.model.optimizer.lr, learning_rate)
                    self.counting = 0
                    self.c_stop = self.c_stop + 1
                    self.counting = 0
                    if self.bestepoch_boolean:
                        self.model.set_weights(
                            self.setbetter_weights)
                    else:
                        if validation_loss < self.min_validloss:
                            self.min_validloss = validation_loss
                else:
                    self.counting = self.counting + 1
        else:
            condition = 'val_loss'
            if validation_loss < self.min_validloss:
                self.min_validloss = validation_loss
                self.setbetter_weights = self.model.get_weights()
                self.counting = 0
                self.c_stop = 0
                color = (0, 255, 0)
                self.lowestloss_epoch = epoch + 1
            else:
                if self.counting >= self.sufferance - 1:
                    color = (245, 170, 66)
                    learning_rate = learning_rate * self.delta_lr_reduce
                    self.c_stop = self.c_stop + 1
                    self.counting = 0
                    tf.keras.backend.set_value(self.model.optimizer.lr, learning_rate)
                    if self.bestepoch_boolean:
                        self.model.set_weights(self.setbetter_weights)
                else:
                    self.counting = self.counting + 1
                if train_accuracy > self.max_trainacc:
                    self.max_trainacc = train_accuracy
        message = f'{str(epoch + 1):^3s}/{str(self.epochs):4s} {loss:^9.3f}{train_accuracy * 100:^9.3f}{validation_loss:^9.5f}{validation_accuracy * 100:^9.3f}{actual_lr:^9.5f}{learning_rate:^9.5f}{condition:^11s}{endurance:^8.2f}'
        text_design(message, color, (55, 65, 80))
        if self.c_stop > self.sufferance_stop - 1:
            message = f' Szkolenie zostało zatrzymane w epoce {epoch + 1} po {self.sufferance_stop} korektach szybkości uczenia się bez poprawy'
            text_design(message, (0, 255, 255), (55, 65, 80))
            self.model.stop_training = True
        else:
            if self.epoch_rethinking != None:
                if epoch + 1 >= self.epoch_rethinking:
                    message = 'F - zakończ szkolenie. M - dostosuj model. Liczba całkowita = liczba epok przed ponownym żądaniem'
                    text_design(message, (0, 255, 255), (55, 65, 80))
                    decision = input('')
                    if decision == 'F' or decision == 'f':
                        message = f'Szkolenie zostało zatrzymane w epoce {epoch + 1} z powodu danych wejściowych użytkownika'
                        text_design(message, (0, 255, 255), (55, 65, 80))
                        self.model.stop_training = True
                    elif decision == 'M' or decision == 'm':
                        message = 'Ustawienie modelu m_base jako możliwego do trenowania w celu dostrojenia modelu'
                        self.m_base.trainable = True
                        text_design(message, (0, 255, 255), (55, 65, 80))
                        message = '{0:^10s}{1:^7s}{2:^7s}{3:^9s}{4:^1s}{5:^7s}{6:^7s}{7:^7s}{8:^7s}'.format('Epoka', 'Strata', 'Dokładność ', 'V_Strata', 'V_Dokładność', 'LR', 'Następny_LR ', 'Condition ',  'Trwanie')
                        text_design(message, (244, 252, 3), (55, 65, 80))
                        self.counting = 0
                        self.c_stop = 0
                        self.epoch_rethinking = epoch + 1 + self.epoch_rethinking_initial
                    else:
                        decision = int(decision)
                        self.epoch_rethinking += decision
                        message = f' Trening będzie trwał do epoki ' + str(self.epoch_rethinking)
                        text_design(message, (0, 255, 255), (55, 65, 80))
                        message = '{0:^10s}{1:^7s}{2:^7s}{3:^9s}{4:^1s}{5:^7s}{6:^7s}{7:^7s}{8:^7s}'.format('Epoka', 'Strata', 'Dokładność ', 'V_Strata', 'V_Dokładność', 'LR', 'Następny_LR ', 'Condition ',  'Trwanie')
                        text_design(message, (244, 252, 3), (55, 65, 80))
epochs = 1
batch_per_epoch = train_stages
epoch_rethinking = None
bestepoch_boolean = True
sufferance = 1
sufferance_stop = 3
delta_lr_reduce = .5
threshold_accuracy = .9
callbacks = [LRA(model=model, m_base=m_base, sufferance=sufferance, sufferance_stop=sufferance_stop, threshold_accuracy=threshold_accuracy, delta_lr_reduce=delta_lr_reduce, bestepoch_boolean=bestepoch_boolean, batch_per_epoch=batch_per_epoch, initial_epoch=0, epochs=epochs, epoch_rethinking=epoch_rethinking)]
history = model.fit(x=train_generator, epochs=epochs, verbose=0, callbacks=callbacks, validation_data=validation_generator, validation_steps=None, shuffle=False, initial_epoch=0)
m_name = 'm'
def tr_plot(tr_data, start_epoch):
    tacc=tr_data.history['accuracy']
    tloss=tr_data.history['loss']
    vacc=tr_data.history['val_accuracy']
    vloss=tr_data.history['val_loss']
    Epoch_count=len(tacc)+ start_epoch
    Epochs=[]
    for i in range (start_epoch ,Epoch_count):
        Epochs.append(i+1)
    index_loss=np.argmin(vloss)
    val_lowest=vloss[index_loss]
    index_acc=np.argmax(vacc)
    acc_highest=vacc[index_acc]
    plt.style.use('fivethirtyeight')
    sc_label='najlepsza epoka= '+ str(index_loss+1 +start_epoch)
    vc_label='najlepsza epoka= '+ str(index_acc + 1+ start_epoch)
    fig,axes=plt.subplots(nrows=1, ncols=2, figsize=(20,8))
    axes[0].plot(Epochs,tloss, 'r', label='Strata treningu')
    axes[0].plot(Epochs,vloss,'g',label='Strata walidacji' )
    axes[0].scatter(index_loss+1 +start_epoch,val_lowest, s=150, c= 'blue', label=sc_label)
    axes[0].set_title('Strata treningu i walidacji')
    axes[0].set_xlabel('Epoki')
    axes[0].set_ylabel('Strata')
    axes[0].legend()
    axes[1].plot (Epochs,tacc,'r',label= 'Dokładność treningu')
    axes[1].plot (Epochs,vacc,'g',label= 'Dokładność walidacji')
    axes[1].scatter(index_acc+1 +start_epoch,acc_highest, s=150, c= 'blue', label=vc_label)
    axes[1].set_title('Dokładność treningu i walidacji')
    axes[1].set_xlabel('Epoki')
    axes[1].set_ylabel('Dokładność')
    axes[1].legend()
    plt.tight_layout
    plt.show()
def print_info( test_gen, preds, print_code, save_dir, subject ):
    class_dict=test_gen.class_indices
    labels= test_gen.labels
    file_names= test_gen.filenames
    error_list=[]
    true_class=[]
    pred_class=[]
    prob_list=[]
    new_dict={}
    error_indices=[]
    y_pred=[]
    for key,value in class_dict.items():
        new_dict[value]=key             # dictionary {integer of class number: string of class name}
    # store new_dict as a text fine in the save_dir
    classes=list(new_dict.values())     # list of string of class names
    errors=0
    for i, p in enumerate(preds):
        pred_index=np.argmax(p)
        true_index=labels[i]  # labels are integer values
        if pred_index != true_index: # a misclassification has occurred
            error_list.append(file_names[i])
            true_class.append(new_dict[true_index])
            pred_class.append(new_dict[pred_index])
            prob_list.append(p[pred_index])
            error_indices.append(true_index)
            errors=errors + 1
        y_pred.append(pred_index)
    if print_code !=0:
        if errors>0:
            if print_code>errors:
                r=errors
            else:
                r=print_code
            msg='{0:^28s}{1:^28s}{2:^28s}{3:^16s}'.format('Plik', 'Przewidywane' , 'Prawidłowo', 'Prawdopodobieństwo')
            text_design(msg, (0,255,0),(55,65,80))
            for i in range(r):
                split1=os.path.split(error_list[i])
                split2=os.path.split(split1[0])
                fname=split2[1] + '/' + split1[1]
                msg='{0:^28s}{1:^28s}{2:^28s}{3:4s}{4:^6.4f}'.format(fname, pred_class[i],true_class[i], ' ', prob_list[i])
                text_design(msg, (255,255,255), (55,65,60))
                #print(error_list[i]  , pred_class[i], true_class[i], prob_list[i])
        else:
            msg='Przy dokładności 100% nie ma błędów do drukowania'
            text_design(msg, (0,255,0),(55,65,80))
    if errors>0:
        plot_bar=[]
        plot_class=[]
        for  key, value in new_dict.items():
            count=error_indices.count(key)
            if count!=0:
                plot_bar.append(count) # list containg how many times a class c had an error
                plot_class.append(value)   # stores the class
        fig=plt.figure()
        fig.set_figheight(len(plot_class)/3)
        fig.set_figwidth(10)
        plt.style.use('fivethirtyeight')
        for i in range(0, len(plot_class)):
            c=plot_class[i]
            x=plot_bar[i]
            plt.barh(c, x)
            plt.title('Błędy według klas w zestawie testowym')
    y_true= np.array(labels)
    y_pred=np.array(y_pred)
    if len(classes)<= 30:
        # create a confusion matrix
        cm = confusion_matrix(y_true, y_pred )
        length=len(classes)
        if length<8:
            fig_width=8
            fig_height=8
        else:
            fig_width= int(length * .5)
            fig_height= int(length * .5)
        plt.figure(figsize=(fig_width, fig_height))
        sns.heatmap(cm, annot=True, vmin=0, fmt='g', cmap='Blues', cbar=False)
        plt.xticks(np.arange(length)+.5, classes, rotation= 90)
        plt.yticks(np.arange(length)+.5, classes, rotation=0)
        plt.xlabel("Przewidywane")
        plt.ylabel("Prawidłowo")
        plt.title("Matryca zamieszania")
        plt.show()
    clr = classification_report(y_true, y_pred, target_names=classes)
    print("Raport klasyfikacyjny:\n----------------------\n", clr)
def save_model(save_path, model, m_name, train_accuracy,  img_resolution, imgscaler, generator):
    m_finalname = str(m_name +  str(train_accuracy)[:str(train_accuracy).rfind('.') + 3] + '.h5')
    m_savedir = os.path.join(save_path, m_finalname)
    model.save(m_savedir)
    text_design('Model został zapisany jako ' + m_savedir, (0,255,0),(55,65,80))
    width = []
    height = []
    image_scale = []
    class_dict = generator.class_indices
    for i in range(len(class_dict)):
        width.append(img_resolution[1])
        height.append(img_resolution[0])
        image_scale.append(imgscaler)
    S_ID = pd.Series(list(class_dict.values()), name='Indeks_klasy')
    S_Category = pd.Series(list(class_dict.keys()), name='Klasa')
    S_Height = pd.Series(height, name='Wysokość')
    S_Width = pd.Series(width, name='Szerokość')
    S_Scale = pd.Series(image_scale, name='Skala')
    class_amount = pd.concat([S_ID, S_Category, S_Height, S_Width, S_Scale], axis=1)
    file_name = 'klasyfikacja.csv'
    f_savedir = os.path.join(save_path, file_name)
    class_amount.to_csv(f_savedir, index=False)
    text_design('Plik csv z klasyfikacją został zapisany jako ' + f_savedir, (0,255,0),(55,65,80))
    return m_savedir, f_savedir
train_accuracy = model.evaluate(test_generator, batch_size=batch_test, verbose=1, steps=test_stages, return_dict=False)[1] * 100
message = f'Dokładność zestawu testowego wynosi {train_accuracy:5.2f} %'
text_design(message, (0,255,0),(55,65,80))
generator = train_generator
image_scale = 'img*.00784313-1'
mloc, csvloc = save_model(home, model, m_name, train_accuracy, img_resolution, image_scale, generator)
error_matrix = 10
preds = model.predict(test_generator)