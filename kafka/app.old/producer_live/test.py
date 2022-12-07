import base64
f = open("img.jpg", "r")
a = open("img2.jpg", "w")
base64_images = base64.b64encode(f)
#message_string = f.encode('ascii')
#new_image = message_string.decode('ascii')
a.write(new_image)
a.close()

