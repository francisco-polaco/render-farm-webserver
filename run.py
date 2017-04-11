import urllib.request
for file in ("test01.txt", "test02.txt", "test03.txt", "test04.txt", "test05.txt", "wood.txt", "test-texmap.txt"):
    for res in ((256, 144), (640, 360), (768, 432), (1280, 720), (1920, 1080), (3840, 2160)):
        print(file + " @ " + str(res))
        urllib.request.urlopen("http://localhost:8000/r.html?f=" + file + "&sc=" + str(res[0]) + "&sr=" + str(res[1]) + "&wc=" +
                               str(res[0]) + "&wr=" + str(res[1]) + "&coff=0&roff=0").read()

