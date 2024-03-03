<img src="/images/logo.png" width="72" height="72">

# Faces Gallery

Extracts faces from images inside your gallery and offers an overview of the analysis. 

| Feed                                     | Attributes                                    | Attributes search                                           | Detailed view                                                |
|------------------------------------------|-----------------------------------------------|-------------------------------------------------------------|--------------------------------------------------------------|
| ![Feed](./images/faces_gallery_main.png) | ![Attributes](./images/faces_gallery_tag.png) | ![Attributes search](./images/faces_gallery_eyeglasses.png) | ![Detailed view](./images/faces_gallery_detailed_person.png) |

## Technologies

The application runs on **Android Lollipop and above**.

The main technologies used in the project:

- Kotlin + [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose): a robust technology to build UI
- [Tensorflow Lite](https://www.tensorflow.org/lite/android): Framework for running ML models on mobile

## Pre-work

Facial Attributes extraction with [CelebA](https://mmlab.ie.cuhk.edu.hk/projects/CelebA.html).

There are not much pretrained weights/models that are able to extract facial attributes. Moreover, I had
a few additional hard and soft requirements: it should be small _(below 40Mb)_ as I run it on edge devices
and big models will likely lead to [OOM](https://developer.android.com/topic/performance/memory), it should
extract facial attributes I am interested in _(at least beard or eyeglasses), and, ideally, it should be
a Keras or TFLite model. At least, I was not able to find any that satisfy the criteria 😅

At this point I decided to train my own model. I decided to stop on 4 facial attributes from CelebA:
**🕶️ eyeglasses, 🧔‍♂️ beard, 🙂 smiling, and mustache**.

### CelebA preprocessing

Raw CelebA images are too noisy and contains too much details, therefore I preprocessed the dataset.
I used OpenCV and DLib to detect faces, and saved them in a separate folder. Some images **had more than
one face**, for example, a person and a bodyguard. There were about 50 of such images and I removed them
from the final dataset.

You can see the examples of processed images below:

| Example 1                                      | Example 2                                      | Example 3                                      | Example 4                                      |
|------------------------------------------------|------------------------------------------------|------------------------------------------------|------------------------------------------------|
| ![Example 1](./images/celeba_pretrained_1.jpg) | ![Example 2](./images/celeba_pretrained_2.jpg) | ![Example 3](./images/celeba_pretrained_3.jpg) | ![Example 4](./images/celeba_pretrained_4.jpg) |

### Training

I trained the model in the following settings:

- [MobileNetV3 Large](https://keras.io/api/applications/mobilenet/)
- No pretrained weights
- 100 epochs

### Results

- Accuracy: 0.9551
- F1 Score: 0.9359
- Precision: 0.9478
- Recall: 0.9257

| Example 1                                     | Example 2                                     | Example 3                                     |
|-----------------------------------------------|-----------------------------------------------|-----------------------------------------------|
| ![Example 1](./images/results_example_1.jpeg) | ![Example 2](./images/results_example_2.jpeg) | ![Example 3](./images/results_example_3.jpeg) |


⚠️ The notebooks with pre-processing and training scripts can be found in [my gists](https://gist.github.com/st235/b3e658f383404acce551d8d7374a61bc).
⚠️⚠️ Pre-trained weights and TF Lite models can be found in [my Google Drive](https://drive.google.com/drive/folders/1y8XXubvsFeie4qDxjQEMzf3dV7uQFq8D?usp=sharing)

## Misc

### Faces Gallery Youtube Playlist

Playlist with all demo videos is available here: [https://youtube.com/playlist?list=PLucKuGqiOAE9fzt8duUc5qnq8mX8c6ReO&si=ZIqqumO2ytc7DrRv](https://youtube.com/playlist?list=PLucKuGqiOAE9fzt8duUc5qnq8mX8c6ReO&si=ZIqqumO2ytc7DrRv)

Videos from the playlist are:

- **Main Flow Demo**: https://youtu.be/IaZfJUDsUow?si=5CkpuchyM09qzpxH
- **Photos review**: https://youtu.be/652KHTUCkZk?si=dqetxw9qznth_iVh
- **Adding a new photo**: https://youtu.be/o1CulOjHrEo?si=A9HS7jy2v5NVejZu
- **Attributes Search**: https://youtu.be/iPUsTKsxL9M?si=XegyJUpExNBxjUi8
- **Person Search**: https://youtu.be/uAqx7uraQlM?si=PyOueC05wW4tqO0a

### Evaluation Criteria List

