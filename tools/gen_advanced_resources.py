#!/usr/bin/env python3
import json, struct, zlib
from pathlib import Path

ROOT=Path(__file__).resolve().parent.parent
RES=ROOT/"src/main/resources"
MOD="aetherium"

def write_json(path,data):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+"\n",encoding="utf-8")

def png(path,w,h,pixel):
    path.parent.mkdir(parents=True,exist_ok=True)
    raw=b"".join(b"\x00"+b"".join(bytes(pixel(x,y)) for x in range(w)) for y in range(h))
    def chunk(t,d):
        return struct.pack(">I",len(d))+t+d+struct.pack(">I",zlib.crc32(t+d)&0xffffffff)
    data=b"\x89PNG\r\n\x1a\n"+chunk(b"IHDR",struct.pack(">IIBBBBB",w,h,8,6,0,0,0))+chunk(b"IDAT",zlib.compress(raw,9))+chunk(b"IEND",b"")
    path.write_bytes(data)

def icon(base,accent):
    def px(x,y):
        if not (2<=x<=13 and 2<=y<=13): return (0,0,0,0)
        edge=x in (2,13) or y in (2,13)
        if edge: return tuple(base)+(255,)
        if (x+y)%7==0 or (x==7 and 5<=y<=10): return tuple(accent)+(255,)
        return tuple(min(255,v+16) for v in base)+(255,)
    return px

def block(base,accent):
    def px(x,y):
        if x==0 or y==0: return tuple(max(0,v-12) for v in base)+(255,)
        if x==15 or y==15: return tuple(max(0,v-22) for v in base)+(255,)
        if (x in (3,12) and 3<=y<=12) or (y in (3,12) and 3<=x<=12):
            return tuple(accent)+(255,)
        return tuple(base)+(255,)
    return px

def armor_texture():
    def px(x,y):
        # restrained graphite plate with teal/amber/violet seams
        c=(47,51,57,255)
        if 7<=x<=13 and 4<=y<=11: c=(64,70,77,255)
        if x in (9,10) and y in range(4,12): c=(72,194,184,255)
        if 20<=x<=43 and 4<=y<=25: c=(55,59,66,255)
        if x in (23,40) and 7<=y<=22: c=(235,123,45,255)
        if 8<=x<=27 and 20<=y<=30: c=(52,55,61,255)
        if 10<=x<=25 and y in (21,29): c=(147,89,221,255)
        return c
    return px

items={
 "research_fragment":("Research Fragment","قطعه پژوهشی"),
 "cipher_plate":("Cipher Plate","صفحه رمزگذاری"),
 "catalyst_shard":("Catalyst Shard","تکه کاتالیزور"),
 "stabilizer_shard":("Stabilizer Shard","تکه تثبیت‌کننده"),
 "aetherium_blueprint":("Aetherium Blueprint","نقشه آتریوم"),
 "aetheric_core":("Aetheric Core","هسته آتریکی"),
 "resonant_cell":("Resonant Cell","سلول تشدیدی"),
 "aetherium_vessel_helmet":("Aetherium Vessel Helmet","کلاه‌خود Aetherium Vessel"),
 "aetherium_vessel_chestplate":("Aetherium Vessel Chestplate","سینه‌پوش Aetherium Vessel"),
 "aetherium_vessel_leggings":("Aetherium Vessel Leggings","شلوار Aetherium Vessel"),
 "aetherium_vessel_boots":("Aetherium Vessel Boots","چکمه Aetherium Vessel"),
 "aetherium_everpick":("Aetherium Everpick","کلنگ جاودانه آتریوم"),
}
blocks={
 "ancient_research_bricks":("Ancient Research Bricks","آجرهای پژوهش باستانی",(54,56,62),(64,155,145)),
 "research_console":("Research Console","کنسول پژوهش",(48,52,60),(72,194,184)),
 "resonance_furnace":("Resonance Furnace","کوره تشدید",(58,52,50),(235,123,45)),
 "catalyst_altar":("Catalyst Altar","محراب کاتالیزور",(62,45,43),(255,145,48)),
 "stabilization_pillar":("Stabilization Pillar","ستون تثبیت",(51,46,63),(150,90,225)),
 "aetheric_assembly_station":("Aetheric Assembly Station","ایستگاه مونتاژ آتریکی",(47,54,60),(70,220,204)),
 "aetherium_frame":("Aetherium Frame","قاب آتریوم",(57,61,68),(94,107,122)),
 "aetherium_block":("Aetherium Block","بلوک آتریوم",(66,72,82),(76,214,202)),
}

en={}
fa={}
elang=RES/f"assets/{MOD}/lang/en_us.json"
flang=RES/f"assets/{MOD}/lang/fa_ir.json"
if elang.exists(): en=json.loads(elang.read_text(encoding="utf-8"))
if flang.exists(): fa=json.loads(flang.read_text(encoding="utf-8"))

for k,(name,fa_name) in items.items():
    en[f"item.{MOD}.{k}"]=name
    fa[f"item.{MOD}.{k}"]=fa_name
    if k.startswith("aetherium_vessel_"):
        png(RES/f"assets/{MOD}/textures/item/{k}.png",16,16,icon((54,60,69),(72,194,184)))
    else:
        png(RES/f"assets/{MOD}/textures/item/{k}.png",16,16,icon((70,76,84),(72,194,184)))
    write_json(RES/f"assets/{MOD}/models/item/{k}.json",{
        "parent":"minecraft:item/generated",
        "textures":{"layer0":f"{MOD}:item/{k}"}
    })
    write_json(RES/f"assets/{MOD}/items/{k}.json",{
        "model":{"type":"minecraft:model","model":f"{MOD}:item/{k}"}
    })

for k,(name,fa_name,base,accent) in blocks.items():
    en[f"block.{MOD}.{k}"]=name
    fa[f"block.{MOD}.{k}"]=fa_name
    png(RES/f"assets/{MOD}/textures/block/{k}.png",16,16,block(base,accent))
    write_json(RES/f"assets/{MOD}/models/block/{k}.json",{
        "parent":"minecraft:block/cube_all",
        "textures":{"all":f"{MOD}:block/{k}"}
    })
    write_json(RES/f"assets/{MOD}/blockstates/{k}.json",{
        "variants":{"":{"model":f"{MOD}:block/{k}"}}
    })
    write_json(RES/f"assets/{MOD}/items/{k}.json",{
        "model":{"type":"minecraft:model","model":f"{MOD}:block/{k}"}
    })

en.update({
 "message.aetherium.fragment_found":"A memory surfaced from the stone.",
 "message.aetherium.fragment_hint":"Its final line reads: 'What was divided among three worlds was once one.'",
 "message.aetherium.vault_trace":"The fragment resonates toward something buried nearby.",
 "message.aetherium.research_found":"The console still remembers one instruction.",
 "message.aetherium.three_worlds":"Overworld. Nether. End. Three pieces of one process.",
 "message.aetherium.catalyst_trace":"The research note points toward a furnace beneath the red sky.",
 "message.aetherium.catalyst_found":"The altar yields a dormant Catalyst Shard.",
 "message.aetherium.stabilizer_trace":"The last fragment points toward a place where the void is made stable.",
 "message.aetherium.stabilizer_found":"The site yields an End Stabilizer Shard.",
 "message.aetherium.blueprint_complete":"The missing blueprint reconstructs itself.",
 "message.aetherium.purified":"The furnace settles. The material is finally purified.",
 "message.aetherium.furnace_hint":"The furnace needs Raw Aetherite Residue.",
 "message.aetherium.no_memory":"The console remains silent.",
 "message.aetherium.catalyst_locked":"The shrine reacts to the research trail, not raw curiosity.",
 "message.aetherium.stabilizer_locked":"The site rejects an incomplete sequence.",
 "message.aetherium.assembly_locked":"The station requires the completed blueprint, a Core and Resonant Alloy.",
 "message.aetherium.assembly_complete":"The Aetherium Vessel takes form.",
})
fa.update({
 "message.aetherium.fragment_found":"خاطره‌ای از دل سنگ بیرون آمد.",
 "message.aetherium.fragment_hint":"خط آخر نوشته: «آنچه میان سه جهان تقسیم شد، زمانی یکی بود.»",
 "message.aetherium.vault_trace":"قطعه به چیزی مدفون در نزدیکی واکنش نشان می‌دهد.",
 "message.aetherium.research_found":"کنسول هنوز یک دستور را به خاطر دارد.",
 "message.aetherium.three_worlds":"Overworld. Nether. End. سه بخش از یک فرایند.",
 "message.aetherium.catalyst_trace":"یادداشت پژوهشی به کوره‌ای زیر آسمان سرخ اشاره می‌کند.",
 "message.aetherium.catalyst_found":"محراب یک تکه کاتالیزور خاموش می‌دهد.",
 "message.aetherium.stabilizer_trace":"آخرین قطعه به جایی اشاره می‌کند که در آن خلأ پایدار شده.",
 "message.aetherium.stabilizer_found":"این مکان یک تکه تثبیت‌کنندهٔ اند می‌دهد.",
 "message.aetherium.blueprint_complete":"نقشهٔ گمشده دوباره کامل شد.",
 "message.aetherium.purified":"کوره آرام می‌شود. ماده بالاخره خالص شد.",
 "message.aetherium.furnace_hint":"کوره به پسماند خام اتریت نیاز دارد.",
 "message.aetherium.no_memory":"کنسول ساکت می‌ماند.",
 "message.aetherium.catalyst_locked":"محراب به مسیر پژوهش واکنش نشان می‌دهد، نه کنجکاوی خام.",
 "message.aetherium.stabilizer_locked":"مکان یک زنجیرهٔ ناقص را نمی‌پذیرد.",
 "message.aetherium.assembly_locked":"ایستگاه به نقشهٔ کامل، یک هسته و آلیاژ تشدیدی نیاز دارد.",
 "message.aetherium.assembly_complete":"Aetherium Vessel شکل می‌گیرد.",
})
write_json(elang,en); write_json(flang,fa)

# Armor equipment assets + all three texture layers required by 26.2.
png(RES/f"assets/{MOD}/textures/entity/equipment/humanoid/aetherium_vessel.png",64,32,armor_texture())
png(RES/f"assets/{MOD}/textures/entity/equipment/humanoid_leggings/aetherium_vessel.png",64,32,armor_texture())
png(RES/f"assets/{MOD}/textures/entity/equipment/humanoid_baby/aetherium_vessel.png",64,32,armor_texture())
write_json(RES/f"assets/{MOD}/equipment/aetherium_vessel.json",{
 "layers":{
   "humanoid":[{"texture":f"{MOD}:aetherium_vessel"}],
   "humanoid_baby":[{"texture":f"{MOD}:aetherium_vessel"}],
   "humanoid_leggings":[{"texture":f"{MOD}:aetherium_vessel"}]
 }
})

# Repair tag
write_json(RES/f"data/{MOD}/tags/item/repairs_aetherium_vessel.json",{
 "replace":False,
 "values":[f"{MOD}:resonant_cell",f"{MOD}:resonant_alloy"]
})

# Recipes: discovery chain stays readable but final gear costs the reconstructed technology.
def shapeless(name,ingredients,result,count=1):
    write_json(RES/f"data/{MOD}/recipe/{name}.json",{
      "type":"minecraft:crafting_shapeless",
      "category":"misc",
      "ingredients":ingredients,
      "result":{"id":result,"count":count}
    })

shapeless("cipher_plate",[f"{MOD}:ancient_memory_fragment","minecraft:copper_ingot", "minecraft:redstone"],f"{MOD}:cipher_plate")
shapeless("resonant_cell",[f"{MOD}:purified_aetherite",f"{MOD}:catalyst_shard",f"{MOD}:stabilizer_shard","minecraft:echo_shard"],f"{MOD}:resonant_cell")
shapeless("aetheric_core",[f"{MOD}:purified_aetherite",f"{MOD}:resonant_cell",f"{MOD}:catalyst_shard",f"{MOD}:stabilizer_shard","minecraft:echo_shard"],f"{MOD}:aetheric_core")
shapeless("aetherium_blueprint",[f"{MOD}:research_fragment",f"{MOD}:cipher_plate",f"{MOD}:stabilizer_shard"],f"{MOD}:aetherium_blueprint")
shapeless("everpick",[f"{MOD}:aetheric_core",f"{MOD}:resonant_cell","minecraft:diamond",f"{MOD}:resonant_alloy"],f"{MOD}:aetherium_everpick")

for name,pattern in {
 "helmet":["RRR","RAR"," A "],
 "chestplate":["RAR","RRR","RRR"],
 "leggings":["RRR","RAR","R R"],
 "boots":["R R","RAR"]
}.items():
    keys={"R":{"item":f"{MOD}:resonant_alloy"},"A":{"item":f"{MOD}:aetheric_core"}}
    ingredients=[]
    for row in pattern:
        for ch in row:
            if ch in keys: ingredients.append(keys[ch])
    shapeless(f"vessel_{name}",ingredients,f"{MOD}:aetherium_vessel_{name}")

print("Advanced Aetherium resources generated.")
