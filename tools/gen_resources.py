#!/usr/bin/env python3
import json, os, random, struct, zlib
ROOT=os.path.dirname(os.path.dirname(os.path.abspath(__file__))); RES=os.path.join(ROOT,'src','main','resources'); MOD='aetherium'
ITEMS={'raw_aetherite_residue':('Raw Aetherite Residue','پسماند خام اتریت',(64,200,190)),'fractured_residue':('Fractured Residue','پسماند شکسته',(90,120,120)),'purified_aetherite':('Purified Aetherite','اتریت خالص‌شده',(130,240,225)),'nether_catalyst':('Nether Catalyst','کاتالیزور نِدر',(235,120,40)),'end_stabilizer':('End Stabilizer','تثبیت‌کننده‌ی اند',(150,90,235)),'dense_alloy':('Dense Aetherium Alloy','آلیاژ اتریوم چگال',(120,125,140)),'light_alloy':('Light Aetherium Alloy','آلیاژ اتریوم سبک',(190,235,245)),'conductive_alloy':('Conductive Aetherium Alloy','آلیاژ اتریوم رسانا',(220,140,80)),'stable_alloy':('Stable Aetherium Alloy','آلیاژ اتریوم پایدار',(140,110,220)),'resonant_alloy':('Resonant Aetherium Alloy','آلیاژ اتریوم تشدیدی',(70,220,200)),'resonance_extractor':('Resonance Extractor','استخراج‌گر تشدیدی',(64,224,208))}
BLOCKS={'aetherite_residue_ore':('Aetherite Residue Ore','سنگ پسماند اتریت',(58,60,68),101),'nether_catalyst_vein':('Nether Catalyst Vein','رگه‌ی کاتالیزور نِدر',(84,42,44),202),'end_stabilizer_deposit':('End Stabilizer Deposit','ذخیره‌ی تثبیت‌کننده‌ی اند',(214,218,150),303)}
def W(p,o):
 p=os.path.join(RES,p); os.makedirs(os.path.dirname(p),exist_ok=True); open(p,'w',encoding='utf8').write(json.dumps(o,ensure_ascii=False,indent=2)+'\n')
def PNG(p,c,seed=1,transparent=False):
 r=random.Random(seed); px=[]
 for y in range(16):
  row=[]
  for x in range(16):
   if transparent and not ((x-7.5)/5)**2+((y-8)/5.5)**2<=1: row.append((0,0,0,0)); continue
   v=r.randint(-10,10); row.append((max(0,min(255,c[0]+v)),max(0,min(255,c[1]+v)),max(0,min(255,c[2]+v)),255))
  px.append(row)
 raw=b''.join(b'\0'+b''.join(bytes(q) for q in row) for row in px)
 def ch(t,d): return struct.pack('>I',len(d))+t+d+struct.pack('>I',zlib.crc32(t+d)&0xffffffff)
 data=b'\x89PNG\r\n\x1a\n'+ch(b'IHDR',struct.pack('>IIBBBBB',16,16,8,6,0,0,0))+ch(b'IDAT',zlib.compress(raw,9))+ch(b'IEND',b'')
 p=os.path.join(RES,p); os.makedirs(os.path.dirname(p),exist_ok=True); open(p,'wb').write(data)
def main():
 en={}; fa={}
 for n,(a,b,c) in ITEMS.items():
  en[f'item.{MOD}.{n}']=a; fa[f'item.{MOD}.{n}']=b; PNG(f'assets/{MOD}/textures/item/{n}.png',c,10+len(n),n=='resonance_extractor'); W(f'assets/{MOD}/models/item/{n}.json',{'parent':'minecraft:item/handheld' if n=='resonance_extractor' else 'minecraft:item/generated','textures':{'layer0':f'{MOD}:item/{n}'}}); W(f'assets/{MOD}/items/{n}.json',{'model':{'type':'minecraft:model','model':f'{MOD}:item/{n}'}})
 for n,(a,b,c,s) in BLOCKS.items():
  en[f'block.{MOD}.{n}']=a; fa[f'block.{MOD}.{n}']=b; PNG(f'assets/{MOD}/textures/block/{n}.png',c,s); W(f'assets/{MOD}/models/block/{n}.json',{'parent':'minecraft:block/cube_all','textures':{'all':f'{MOD}:block/{n}'}}); W(f'assets/{MOD}/blockstates/{n}.json',{'variants':{'':{'model':f'{MOD}:block/{n}'}}}); W(f'assets/{MOD}/items/{n}.json',{'model':{'type':'minecraft:model','model':f'{MOD}:block/{n}'}})
 W(f'assets/{MOD}/lang/en_us.json',en); W(f'assets/{MOD}/lang/fa_ir.json',fa)
 W('data/minecraft/tags/block/mineable/pickaxe.json',{'replace':False,'values':[f'{MOD}:{x}' for x in BLOCKS]}); W('data/minecraft/tags/block/needs_iron_tool.json',{'replace':False,'values':[f'{MOD}:aetherite_residue_ore']}); W('data/minecraft/tags/block/needs_diamond_tool.json',{'replace':False,'values':[f'{MOD}:{x}' for x in ('nether_catalyst_vein','end_stabilizer_deposit')]})
 def loot(b,i): W(f'data/{MOD}/loot_table/blocks/{b}.json',{'type':'minecraft:block','pools':[{'rolls':1,'conditions':[{'condition':'minecraft:survives_explosion'}],'entries':[{'type':'minecraft:item','name':f'{MOD}:{i}'}]}]})
 W(f'data/{MOD}/loot_table/blocks/aetherite_residue_ore.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:alternatives','children':[{'type':'minecraft:item','name':f'{MOD}:raw_aetherite_residue','conditions':[{'condition':'minecraft:match_tool','predicate':{'items':[f'{MOD}:resonance_extractor']}}]},{'type':'minecraft:item','name':f'{MOD}:fractured_residue'}]}]}]}); loot('nether_catalyst_vein','nether_catalyst'); loot('end_stabilizer_deposit','end_stabilizer')
 def recipe(n,o): W(f'data/{MOD}/recipe/{n}.json',o)
 R=lambda x:f'{MOD}:{x}'; P=R('purified_aetherite'); C=R('nether_catalyst'); S=R('end_stabilizer')
 recipe('resonance_extractor',{'type':'minecraft:crafting_shaped','pattern':['DED','CNC','RSR'],'key':{'D':'minecraft:diamond','E':'minecraft:echo_shard','C':'minecraft:copper_ingot','N':'minecraft:netherite_scrap','R':'minecraft:redstone','S':'minecraft:stick'},'result':{'id':R('resonance_extractor'),'count':1}}); recipe('purified_aetherite_from_raw',{'type':'minecraft:blasting','ingredient':R('raw_aetherite_residue'),'result':{'id':P,'count':1},'experience':1.0,'cookingtime':300})
 def sh(n,ins,out): recipe(n,{'type':'minecraft:crafting_shapeless','ingredients':ins,'result':{'id':out,'count':1}})
 sh('raw_from_fractured',[R('fractured_residue')]*5+['minecraft:copper_ingot'],R('raw_aetherite_residue')); sh('dense_alloy',[P,P,C,S,'minecraft:netherite_scrap'],R('dense_alloy')); sh('light_alloy',[P,C,S,'minecraft:amethyst_shard','minecraft:quartz'],R('light_alloy')); sh('conductive_alloy',[P,C,S,'minecraft:copper_ingot','minecraft:copper_ingot'],R('conductive_alloy')); sh('stable_alloy',[P,C,S,S,'minecraft:obsidian'],R('stable_alloy')); sh('resonant_alloy',[P,C,S,'minecraft:echo_shard','minecraft:redstone'],R('resonant_alloy'))
 print('Aetherium resources generated')
if __name__=='__main__': main()
