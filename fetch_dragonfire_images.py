import os
import urllib.request
import io
from PIL import Image

HEADERS_GENERAL = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36'
}

HEADERS_WIKI = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/119.0',
    'Referer': 'https://gameofthrones.fandom.com/',
    'Accept': 'image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8'
}

DRAGONFIRE_URLS = {
    "caraxes": "https://gotdragonfire.com/_astro/caraxes-portrait-square.imYG0ELv_2tDwuB.webp",
    "vhagar": "https://gotdragonfire.com/_astro/vhagar-portrait-square.C38gcr8g_2oxYfz.webp",
    "syrax": "https://gotdragonfire.com/_astro/syrax-portrait-square.A-f_-EBT_16VKkn.webp",
    "seasmoke": "https://gotdragonfire.com/_astro/seasmoke-portrait-square.CaH0AXlH_ZmpLmQ.webp",
    "vermithor": "https://gotdragonfire.com/_astro/vermithor-portrait-square.q-qFN8Dn_1tM7r2.webp",
    "meleys": "https://gotdragonfire.com/_astro/meleys-portrait-square.jYCJWCYX_2uYfaW.webp",
    "sunfyre": "https://gotdragonfire.com/_astro/sunfyre-portrait-square.AF9mSpti_Z1AYApk.webp",
    "arrax": "https://gotdragonfire.com/_astro/arrax-portrait-square.Bjp-7Ltt_1l8xqS.webp",
    "tessarion": "https://gotdragonfire.com/_astro/tessarion-portrait-square.DWZVjnSP_25bXzT.webp",
    "vermax": "https://gotdragonfire.com/_astro/vermax-portrait-square.CQzAXlik_Z20blE5.webp",
    "robaovejas": "https://gotdragonfire.com/_astro/sheepstealer-portrait-square.D-l6Ok54_Z2wnXSW.webp",
    "moondancer": "https://gotdragonfire.com/_astro/moondancer-portrait-square.DH5GtJhR_1Ae75a.webp",
}

LORE_HOTD_URLS = {
    "silverwing": "https://static.wikia.nocookie.net/gameofthrones/images/9/98/Silverwing.png/revision/latest",
    "dreamfyre": "https://static.wikia.nocookie.net/gameofthrones/images/3/32/Dreamfyre.png/revision/latest",
    "balerion": "https://static.wikia.nocookie.net/gameofthrones/images/3/3e/Balerion_the_Black_Dread.png/revision/latest",
    "meraxes": "https://static.wikia.nocookie.net/gameofthrones/images/5/53/Meraxes.PNG/revision/latest",
    "canibal": "https://static.wikia.nocookie.net/hieloyfuego/images/1/10/Can%C3%ADbal_by_Artaedra%C2%A9.png/revision/latest",
}

def download_and_save(dragon_name, url, is_wiki=False):
    headers = HEADERS_WIKI if is_wiki else HEADERS_GENERAL
    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = resp.read()
            img = Image.open(io.BytesIO(data))
            
            # Convert to RGBA / RGB and save as PNG
            out_path = os.path.join("images", f"{dragon_name}.png")
            if img.mode not in ("RGB", "RGBA"):
                img = img.convert("RGBA")
            img.save(out_path, "PNG")
            source_tag = "Wiki Official Guide" if is_wiki else "Game of Thrones: Dragonfire"
            print(f"[{source_tag}] Descargada imagen para {dragon_name}: {img.size} -> {out_path}")
            return True
    except Exception as e:
        print(f"Error descargando {dragon_name} desde {url}: {e}")
        return False

def main():
    os.makedirs("images", exist_ok=True)
    
    print("--- 1. Descargando dragones desde Game of Thrones: Dragonfire ---")
    for name, url in DRAGONFIRE_URLS.items():
        download_and_save(name, url, is_wiki=False)
        
    print("\n--- 2. Descargando dragones restantes de la franquicia HotD ---")
    for name, url in LORE_HOTD_URLS.items():
        download_and_save(name, url, is_wiki=True)

    print("\n¡Proceso de descarga completado con éxito!")

if __name__ == "__main__":
    main()
