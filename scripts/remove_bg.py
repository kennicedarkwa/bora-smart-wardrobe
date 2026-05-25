import sys
from pathlib import Path

from PIL import Image
from withoutbg import WithoutBG


def remove_background(input_path: Path, output_path: Path) -> None:
    if not input_path.exists():
        raise FileNotFoundError(f"Eingabedatei nicht gefunden: {input_path}")

    img = Image.open(input_path).convert("RGBA")

    model = WithoutBG.opensource()
    result = model.remove_background(img)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    result.save(output_path, format="PNG")


def main():
    if len(sys.argv) != 3:
        print("Usage: python remove_bg.py <input> <output>", file=sys.stderr)
        sys.exit(2)

    input_path = Path(sys.argv[1])
    output_path = Path(sys.argv[2])

    try:
        remove_background(input_path, output_path)
        print(f"OK: gespeichert als {output_path}")
    except Exception as e:
        print(f"FEHLER: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()