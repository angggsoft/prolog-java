import urllib.request
import json
import subprocess
import os

def get_git_token():
    p = subprocess.Popen(['git', 'credential', 'fill'], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    out, _ = p.communicate('protocol=https\nhost=github.com\n\n')
    for line in out.splitlines():
        if line.startswith('password='):
            return line.split('=', 1)[1]
    return None

def create_github_repo(token, name, description, private=False):
    payload = json.dumps({
        "name": name,
        "description": description,
        "private": private,
        "auto_init": False
    }).encode('utf-8')
    
    req = urllib.request.Request(
        "https://api.github.com/user/repos",
        data=payload,
        headers={
            "Authorization": f"Bearer {token}",
            "User-Agent": "Python",
            "Accept": "application/vnd.github+json",
            "Content-Type": "application/json"
        },
        method="POST"
    )
    
    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode())
            print(f"Repositorio creado exitosamente en GitHub: {data.get('html_url')}")
            return data.get('clone_url')
    except urllib.error.HTTPError as e:
        body = e.read().decode()
        if e.code == 422 and "already exists" in body:
            print("El repositorio ya existe en GitHub.")
            return f"https://github.com/angggsoft/{name}.git"
        else:
            print(f"Error creando repo ({e.code}): {body}")
            return None

def main():
    token = get_git_token()
    if not token:
        print("No se encontró token en Git Credential Manager.")
        return

    clone_url = create_github_repo(
        token,
        "prolog-java",
        "Sistema Experto de Dragones de House of the Dragon conectando Prolog con Java Swing mediante JPL",
        private=False
    )
    
    if not clone_url:
        print("No se pudo obtener la URL del repositorio.")
        return

    # Git commands
    print("\nInicializando repositorio Git local...")
    subprocess.run(["git", "init", "-b", "main"], check=True)
    
    # Configure remote
    res = subprocess.run(["git", "remote"], capture_output=True, text=True)
    if "origin" in res.stdout.split():
        subprocess.run(["git", "remote", "set-url", "origin", clone_url], check=True)
    else:
        subprocess.run(["git", "remote", "add", "origin", clone_url], check=True)

    print("Agregando archivos...")
    subprocess.run(["git", "add", "."], check=True)

    print("Creando commit inicial...")
    subprocess.run(["git", "commit", "-m", "Initial commit: Sistema Experto de Dragones HotD en Prolog y Java Swing"], check=True)

    print("Subiendo a GitHub (git push -u origin main)...")
    subprocess.run(["git", "push", "-u", "origin", "main"], check=True)
    print("\n¡Proyecto subido exitosamente a GitHub!")

if __name__ == "__main__":
    main()
